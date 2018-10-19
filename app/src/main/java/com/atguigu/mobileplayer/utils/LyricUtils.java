package com.atguigu.mobileplayer.utils;

import com.atguigu.mobileplayer.domain.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 作者： 石刚
 * QQ号 342532640
 * 作用:解析歌词工具类
 */
public class LyricUtils {
    private ArrayList<Lyric> lyrics;

    /**
     * 是否存在歌词
     * @return
     */
    public boolean isExistsLyric() {
        return isExistsLyric;
    }

    /**
     * 是否存在歌词
     */
    private boolean isExistsLyric = false;

    /**
     * 得到解析好的歌词列表
     * @return
     */
    public ArrayList<Lyric> getLyrics(){
        return lyrics;
    }
    /**
     * 读取歌词文件
     * @param file/mnt/sdcard/audio/beijingbeijing.txt或lrc
     */
    public void readLyricFile(File file){
         if (file==null||!file.exists()){
             //歌词文件不存在
             lyrics = null;
             isExistsLyric = false;
         }else{
             //歌词文件存在
             //1.解析歌词，一行的读取-解析
             lyrics = new ArrayList<>();
             isExistsLyric = true;
             BufferedReader reader = null;
             try {
                 reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),getCharset(file)));
                 String line = "";
                 while ((line=reader.readLine())!=null) {
                     line = parseLyric(line);//解析完一行，我在这里欢笑
                 }
                 reader.close();
             } catch (Exception e){
                 e.printStackTrace();
             }

             //2.排序
             Collections.sort(lyrics, new Comparator<Lyric>() {
                 @Override
                 public int compare(Lyric lhs, Lyric rhs) {
                     //根据时间戳排序
                     if (lhs.getTimePoint()<rhs.getTimePoint()){
                         return -1;
                     }else if ((lhs).getTimePoint()>rhs.getTimePoint()){
                         return 1;
                     }else{
                         return 0;
                     }
                 }
             });
             //3.计算每句高亮显示的时间
             for (int i = 0; i < lyrics.size(); i++) {
                Lyric oneLyric = lyrics.get(i);
                 if (i+1<lyrics.size()){
                     Lyric twoLyric = lyrics.get(i+1);
                     //第一句的高亮时间等于第二句的时间戳减去第一句的时间戳
                     oneLyric.setSleepTime(twoLyric.getTimePoint()-oneLyric.getTimePoint());
                 }
             }

         }
    }
    /**
     * 判断文件编码
     * @param file 文件
     * @return 编码：GBK,UTF-8,UTF-16LE
     */
    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }
    /**
     * 解析一句歌词
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
    private String parseLyric(String line) {
        //歌词有括号，
        int pos1 = line.indexOf("[");//第一次出现[的位置，0，如果没有返回-1
        int pos2 = line.indexOf("]");//9,如果没有返回-1
        if (pos1 == 0&&pos2!=-1){//肯定是有一句歌词
            //装时间
            long[] times = new long[getCountTag(line)];
            //截取字符串
            String strTime = line.substring(pos1+1,pos2);//02:04.12
            times[0] = strTime2LongTime(strTime);

            String content = line;
            int i = 1;
            while (pos1==0&&pos2!=-1) {
                //[03:37.32][00:59.73]我在这里欢笑----->[00:59.73]我在这里欢笑---->
                content = content.substring(pos2+1);//得到上面这行
                pos1 = content.indexOf("[");//0
                pos2 = content.indexOf("]");//9

                if (pos2 !=-1){
                    strTime = content.substring(pos1+1,pos2);//得到的是 03:37.32
                    times[i] = strTime2LongTime(strTime);

                    if (times[i]==-1){
                        return "";
                    }
                    i++;
                }

            }
            /**
             * 循环结束之后这里面填充了3个long类型
             * 把时间数组和文本关联起来，并且加入到集合中
             */
            Lyric lyric = new Lyric();
            for (int j = 0; j <times.length ; j++) {
                if (times[j]!=0){//有时间戳
                   lyric.setContent(content);
                    lyric.setTimePoint(times[j]);
                    //添加到集合中
                    lyrics.add(lyric);

                    lyric = new Lyric();
                }
            }
            //for循环结束以后返回这个
            return content;//我在这里欢笑
        }

        return null;
    }

    /**
     * 把String类型的时间转换成long类型
     * @param strTime
     * @return
     */
    private long strTime2LongTime(String strTime) {
         long result = -1;
        try {
            //1.把02:04.12按照:切割成02和04.12
            String[] s1 = strTime.split(":");
            //2.把04.12按照.切割成04和12
            String[] s2 = s1[1].split("\\.");//需要转义

            //1.分
            long min = Long.parseLong(s1[0]);
            //2.秒
            long second = Long.parseLong(s2[0]);
            //3.毫秒
            long millisecond = Long.parseLong(s2[1]);
            result = min*60*1000+second*1000+millisecond*10;//0.12*1000=12*10
        } catch (Exception e) {
            e.printStackTrace();
            //如果出错了再把它变成-1
            result = -1;
        }
        return result;
    }

    /**
     * 判断有多少句歌词
     * @param line [02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @return
     */
   private int getCountTag(String line){
       int result = -1;
       String[] left = line.split("\\[");
       String[] right = line.split("\\]");

       if (left.length==0&&right.length==0){
           result=1;
       }else if (left.length>right.length){
           result = left.length;
       }else{
           result = right.length;
       }
       return result;
   }
}
