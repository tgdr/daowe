package edu.buu.daowe.Util;

import com.baidubce.auth.DefaultBceCredentials;
import com.baidubce.services.bos.BosClientConfiguration;

public class BosUtils {

    public static BosClientConfiguration initBosClientConfiguration() {
        BosClientConfiguration config;
        config = new BosClientConfiguration();
        config.setCredentials(new DefaultBceCredentials("9cbacc8d6da34e1c80ab9dc166133901",
                "f9335932243144cb8648bbd2a18a8e37"));   //您的AK/SK
        config.setEndpoint("bj.bcebos.com");    //传入Bucket所在区域域名


        return config;
    }


}
