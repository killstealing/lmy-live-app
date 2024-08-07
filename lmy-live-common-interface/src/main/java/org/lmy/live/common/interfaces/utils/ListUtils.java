package org.lmy.live.common.interfaces.utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static <T> List<List<T>> splitList(List<T> list, int subNum){
        List<List<T>> resultList=new ArrayList<>();
        int size=list.size()/subNum;
        int preIndex=0;
        int lastIndex=0;
        for (int i = 0; i < size; i++) {
            preIndex=subNum*i;
            lastIndex=preIndex+subNum;
            if(lastIndex<=list.size()){
                resultList.add(list.subList(preIndex, lastIndex));
            }else{
                resultList.add(list.subList(preIndex,list.size()));
            }
        }
        return resultList;
    }

    public static void main(String[] args) {
        List<Integer> list=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        System.out.println(splitList(list,10));
    }

}
