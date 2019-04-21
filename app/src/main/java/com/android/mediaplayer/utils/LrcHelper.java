package com.android.mediaplayer.utils;

/**
 * Created by zzw on 2019/3/31.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import android.content.Context;

import com.lauzy.freedom.library.Lrc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcHelper {
    private static final String CHARSET = "utf-8";
    private static final String LINE_REGEX = "((\\[\\d{2}:\\d{2}\\.\\d{2}])+)(.*)";
    private static final String TIME_REGEX = "\\[(\\d{2}):(\\d{2})\\.(\\d{2})]";

    public LrcHelper() {
    }


    public static List<Lrc> parseLrcFromFile(File file) {
        try {
            return parseInputStream(new FileInputStream(file));
        } catch (FileNotFoundException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    private static List<Lrc> parseInputStream(InputStream inputStream) {
        List<Lrc> lrcs = new ArrayList();
        InputStreamReader isr = null;
        BufferedReader br = null;

        try {
            isr = new InputStreamReader(inputStream, "utf-8");
            br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null) {
                List<Lrc> lrcList = parseLrc(line);
                if(lrcList != null && lrcList.size() != 0) {
                    lrcs.addAll(lrcList);
                }
            }

            sortLrcs(lrcs);
            return lrcs;
        } catch (UnsupportedEncodingException var17) {
            var17.printStackTrace();
        } catch (IOException var18) {
            var18.printStackTrace();
        } finally {
            try {
                if(isr != null) {
                    isr.close();
                }

                if(br != null) {
                    br.close();
                }
            } catch (IOException var16) {
                var16.printStackTrace();
            }

        }

        return lrcs;
    }

    private static void sortLrcs(List<Lrc> lrcs) {
        Collections.sort(lrcs, new Comparator<Lrc>() {
            public int compare(Lrc o1, Lrc o2) {
                return (int)(o1.getTime() - o2.getTime());
            }
        });
    }

    private static List<Lrc> parseLrc(String lrcLine) {
        if(lrcLine.trim().isEmpty()) {
            return null;
        } else {
            List<Lrc> lrcs = new ArrayList();
            //System.out.println(lrcLine);
            Matcher matcher = Pattern.compile("((\\[\\d{2}:\\d{2}\\.\\d{3}])+)(.*)").matcher(lrcLine);
            if(!matcher.matches()) {
                //System.out.println("null");
                return null;
            } else {
                String time = matcher.group(1);
                String content = matcher.group(3);
                Matcher timeMatcher = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{3})]").matcher(time);
                //System.out.println(content+"content");

                while(timeMatcher.find()) {
                    String min = timeMatcher.group(1);
                    String sec = timeMatcher.group(2);
                    String mil = timeMatcher.group(3);

                    Lrc lrc = new Lrc();
                    if(content != null && content.length() != 0) {
                        lrc.setTime(Long.parseLong(min) * 60L * 1000L + Long.parseLong(sec) * 1000L + Long.parseLong(mil) );
                        lrc.setText(content);
                        lrcs.add(lrc);
                    }
                }

                return lrcs;
            }
        }
    }

    public static String formatTime(long time) {
        int min = (int)(time / 60000L);
        int sec = (int)(time / 1000L % 60L);
        return adjustFormat(min) + ":" + adjustFormat(sec);
    }

    private static String adjustFormat(int time) {
        return time < 10?"0" + time:time + "";
    }
}
