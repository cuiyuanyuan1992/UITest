package com.qa.common;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class CommandUtils {

    /**
     * 执行shell命令
     * @param command
     * @return 执行结果
     */
    public List<String> execCommand(String command){
        Process process = null;
        List<String> processList = new ArrayList<String>();
        try {
            process = Runtime.getRuntime().exec(command);
            log.info("shell exec ={}",command);
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = input.readLine()) != null) {
                processList.add(line);
            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("shell exec response={}",processList.toString());
        return processList;
    }

    public void sleep(int s){
        try {
            Thread.sleep(s*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
