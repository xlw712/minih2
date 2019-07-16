package com.levin.mh2;

import com.google.common.io.Resources;
import com.levin.mh2.core.MiniHS2;
import com.levin.mh2.util.CommonUtils;
import com.levin.mh2.util.CommonUtils.OSType;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Levin
 * @version 1.0.0
 * @Title MiniH2Start
 * @Description TODO
 * @Date 2019/7/12 16:18
 */
public class MiniH2Start {
    private static MiniHS2 miniHS2 = null;
    private static Connection hs2Conn = null;
    private static String CLASS_PATH;
    private static final String HADOOP_HOME_DIR_ENV = "hadoop.home.dir";
    private static String METASTOREURIS_VARS;
    private static String HIVESERVERURIS_VARS;
    private static String HIVESERVER_USERNAME_VARS;
    private static String HIVESERVER_PASSWORD_VARS;
    private static final Logger logger = Logger.getLogger(MiniH2Start.class);

    public static void setUp() {
        OSType osType = CommonUtils.getOSType();
        switch (osType) {
            case OS_TYPE_WIN:
                CLASS_PATH = Resources.getResource(".").getPath();
                System.getProperties().put(HADOOP_HOME_DIR_ENV, CLASS_PATH);
                break;
        }
    }

    public static void start() throws Exception {
        Class.forName(MiniHS2.getJdbcDriverName());
        HiveConf conf = new HiveConf();
        conf.setBoolVar(HiveConf.ConfVars.HIVE_SUPPORT_CONCURRENCY, false);
        /**
         *
         * below config is start a hiveServer、a miniMrCluster、a metastoreServer
         * if want only start hiveServer can refer below
         *
         * miniHS2 = MiniHS2(hiveConf);
         * miniHS2.start();
         */
        miniHS2 = new MiniHS2.Builder()
                .withMiniMR()
                .withRemoteMetastore()
                .withConf(conf).build();

        Map<String, String> confOverlay = new HashMap<String, String>();
        miniHS2.start(confOverlay);

        METASTOREURIS_VARS = miniHS2.getHiveConf().getVar(HiveConf.ConfVars.METASTOREURIS);
        HIVESERVERURIS_VARS = miniHS2.getJdbcURL();
        HIVESERVER_USERNAME_VARS = System.getProperty("user.name");
        System.out.println("==========================================");
        System.out.println("==========MiniH2Server start !!===========");
        System.out.println("HiveServer-Uris:  " + HIVESERVERURIS_VARS);
        System.out.println("HiveServer-UserNmae:  " + HIVESERVER_USERNAME_VARS);
        System.out.println("MetaStore-Uris:   " + METASTOREURIS_VARS);
        System.out.println("==========================================");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (miniHS2.isStarted()) {
                    miniHS2.stop();
                    System.out.println("MiniH2Server stop !!");
                }
            }
        });
    }


    public static void main(String[] args) {
        setUp();
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
