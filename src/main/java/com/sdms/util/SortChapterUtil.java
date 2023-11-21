package com.sdms.util;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@Component
public class SortChapterUtil {

    public List<Map<String,Object>> otherList = new ArrayList<>();

    public List<Map<String,Object>> sortChapterList(List<Map<String,Object>> chapterList) {

        // 章节list转化为map
        Map<String,Map<String,Object>> chapterMap =  listChangeToMap(chapterList);
        // 获得章节号
        List<String> chapterNum = getChapterNum(chapterMap);
        // 章节号去除点
        Map<Integer,String> chapterNumNoDot =  removeDot(chapterNum);
        // 获取数字最大长度
        assert chapterNumNoDot != null;
        int maxLength = getChapterNumMaxLength(chapterNumNoDot.keySet());
        // 获取补0后的列表
        List<String> fillZeroChapterNum = fillZero(maxLength,chapterNumNoDot);
        // 排序 默认是升序，刚好是我们需要的
        Collections.sort(fillZeroChapterNum);
        // 重组map对象
        return getSortChapterMap(fillZeroChapterNum,chapterMap,chapterNumNoDot);

    }

    public List<Map<String,Object>> getSortChapterMap(List<String> fillZeroChapterNum,Map<String,Map<String,Object>> chapterMap,Map<Integer,String> chapterNotDot){
        if(null == fillZeroChapterNum || fillZeroChapterNum.size() == 0) return null;
        if(null == chapterMap) return null;
        LinkedList<Map<String,Object>> sortChapterList = new LinkedList<>();

        for (int i = 0; i < otherList.size(); i++) {
            String title = (String) otherList.get(i).get("chapter");
            if(title.contains("编制")) {
                sortChapterList.add(otherList.get(i));
                otherList.remove(i--);
            }else if(title.contains("引 言")) {
                sortChapterList.add(otherList.get(i));
                otherList.remove(i--);
            }else if(title.contains("前 言")) {
                sortChapterList.add(otherList.get(i));
                otherList.remove(i--);
            }
        }

        for(String temp:fillZeroChapterNum){
            sortChapterList.add(chapterMap.get(chapterNotDot.get(Integer.parseInt(temp.replace("0", "")))));
        }

        if(!otherList.isEmpty()) {
            for (int i = 0; i < otherList.size(); i++) {
                String title = (String) otherList.get(i).get("chapter");
                if(title.contains("附")){
                    sortChapterList.add(otherList.get(i));
                    otherList.remove(i--);
                }
            }
            sortChapterList.addAll(otherList);
            otherList.clear();
        }


        return sortChapterList;
    }

    /**
     * 补零操作
     */
    public List<String> fillZero(int maxLength, Map<Integer,String> chapterNumNoDot){
        if(null == chapterNumNoDot || chapterNumNoDot.size() ==0) return null;
        List<String> fillZeroList = new ArrayList<>();
        for(Integer key:chapterNumNoDot.keySet()){
            fillZeroList.add(key + getNeedZero(maxLength - (key + "").length()));
        }
        return fillZeroList;
    }

    /**
     * 获得需要0的个数
     */
    public String getNeedZero(int num){
        if(num <1) return "";
        StringBuilder sb = new StringBuilder();
        // 拼凑需要的0
        for(int i=0;i<num;i++){
            sb.append("0");
        }
        return sb.toString();
    }

    /**
     * 返回数组最大值
     */
    public int max(int[] a){
        // 返回数组最大值
        int x;
        int[] aa =new int[a.length];
        System.arraycopy(a,0,aa,0,a.length);
        x=aa[0];
        for(int i=1;i<aa.length;i++){
            if(aa[i]>x){
                x=aa[i];
            }
        }
        return x;
    }

    /**
     * 获得章节号最大长度
     */
    public int getChapterNumMaxLength(Set<Integer> chapterNumNoDot){
        if(null == chapterNumNoDot || chapterNumNoDot.size() == 0) return 0;
        Object[] chapterNumArr = chapterNumNoDot.toArray();
        int[] chapterNum = new int[chapterNumArr.length];
        for(int i=0;i<chapterNumArr.length;i++){
            chapterNum[i] = chapterNumArr[i].toString().length();
        }
        return max(chapterNum);
    }


    /**
     * 去除章节号中的点
     */
    public Map<Integer,String> removeDot(List<String> chapterNumList){
        if(null == chapterNumList || chapterNumList.size() == 0) return null;
        Map<Integer,String> rmDotChapterNumMap = new HashMap<>();
        for (String s : chapterNumList) {
            // 把点替换成空
            rmDotChapterNumMap.put(Integer.parseInt(s.replace(".", "")), s);
        }
        return rmDotChapterNumMap;
    }

    /**
     * 获取章节号
     */
    public List<String> getChapterNum(Map<String,Map<String,Object>> chapterMap){
        if(null == chapterMap) return null;
        return new ArrayList<>(chapterMap.keySet());
    }

    /**
     * 把list转变为map
     */
    public Map<String,Map<String,Object>> listChangeToMap(List<Map<String,Object>> chapterList){
        // 存到map中
        Map<String,Map<String,Object>> chapterMap = new HashMap<>();

        if(null == chapterList || chapterList.size() == 0) return null;
        String regex = "^[1-9][0-9]?\\.?[1-9]?[0-9]*\\.?[1-9]?[0-9]*\\.?[1-9]?[0-9]*\\.?[1-9]?[0-9]*";
        Pattern pattern = Pattern.compile(regex);

        for(Map<String,Object> source:chapterList){
            String chapter = (String) source.get("chapter");
            Matcher matcher = pattern.matcher(chapter);
            if(matcher.find()) {
                chapterMap.put(matcher.group(), source);
            }else {
                otherList.add(source);
            }
        }
        return chapterMap;
    }



}
