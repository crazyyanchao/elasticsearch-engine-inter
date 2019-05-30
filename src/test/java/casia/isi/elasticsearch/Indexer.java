package casia.isi.elasticsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import casia.isi.elasticsearch.operation.search.EsIndexSearch;


public class Indexer {
    public static final String split = "&";
    public static List<String> slaves;
    public static List<String> slavesAll;
    public static List<String> slavesShort;
    public static List<String> slavesEw;

    public static String coreServer;
    public static String slaveServers;
    public static String coreServerAll;
    public static String slaveServersAll;
    public static String coreServerShort;
    public static String slaveServersShort;
    public static String coreServersEw;
    public static String slaveServersEw;

    public static String monitorDataEw;
    public static String monitorEsSmall = "192.168.12.109:9210";
    public static String monitorEsAll;

    public Indexer() {
        super();
    }


    public String getMonitorEsSmall() {
        return monitorEsSmall;
    }

    public void setMonitorEsSmall(String monitorEsSmall) {
        Indexer.monitorEsSmall = monitorEsSmall;
    }

    public static EsIndexSearch getMonitoresSmallIndex(String indexName) {
        String[] server = monitorEsSmall.split(split);
        int index = new Random().nextInt(server.length);
        // news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small,wechat_message_xigua_small,appdata_small
        return new EsIndexSearch(server[index], indexName, "smallAllIndex");
    }


    public String getMonitorEsAll() {
        return monitorEsAll;
    }

    public void setMonitorEsAll(String monitorEsAll) {
        Indexer.monitorEsAll = monitorEsAll;
    }

    public static EsIndexSearch getMonitoresAllIndex(String indexName) {
        String[] server = monitorEsAll.split(split);
        int index = new Random().nextInt(server.length);
        return new EsIndexSearch(server[index], indexName, "smallAllIndex");
    }


    public String getMonitorDataEw() {
        return monitorDataEw;
    }

    public void setMonitorDataEw(String monitorDataEw) {
        Indexer.monitorDataEw = monitorDataEw;
    }

    public static EsIndexSearch getMonitordataEsIndex(String indexName) {
        String[] server = monitorDataEw.split(split);
        int index = new Random().nextInt(server.length);
        return new EsIndexSearch(server[index], indexName, "monitor_data");
    }


    public String getCoreServer() {
        return coreServer;
    }

    public void setCoreServer(String coreServer) {
        Indexer.coreServer = coreServer;
    }


    public String getCoreServerAll() {
        return coreServerAll;
    }

    public void setCoreServerAll(String coreServerAll) {
        Indexer.coreServerAll = coreServerAll;
    }


    public static String getCoreServerShort() {
        return coreServerShort;
    }

    public void setCoreServerShort(String coreServerShort) {
        Indexer.coreServerShort = coreServerShort;
    }


    public String getSlaveServers() {
        return slaveServers;
    }

    public void setSlaveServers(String slaveServers) {
        Indexer.slaveServers = slaveServers;
        Indexer.slaves = new ArrayList<String>();
        if (Indexer.slaveServers != null && !Indexer.slaveServers.isEmpty()) {
            String[] ssus = Indexer.slaveServers.split(Indexer.split);
            if (ssus != null && ssus.length > 0) {
                for (String ssu : ssus) {
                    if (ssu != null && !ssu.isEmpty()) {
                        slaves.add(ssu);
                    }
                }
            }
        }
    }


    public String getSlaveServersAll() {
        return slaveServersAll;
    }

    public void setSlaveServersAll(String slaveServersAll) {
        Indexer.slaveServersAll = slaveServersAll;
        Indexer.slavesAll = new ArrayList<String>();
        if (Indexer.slaveServersAll != null && !Indexer.slaveServersAll.isEmpty()) {
            String[] ssus = Indexer.slaveServersAll.split(Indexer.split);
            if (ssus != null && ssus.length > 0) {
                for (String ssu : ssus) {
                    if (ssu != null && !ssu.isEmpty()) {
                        slavesAll.add(ssu);
                    }
                }
            }
        }
    }


    public String getSlaveServersShort() {
        return slaveServersShort;
    }

    public void setSlaveServersShort(String slaveServersShort) {
        Indexer.slaveServersShort = slaveServersShort;
        Indexer.slavesShort = new ArrayList<String>();
        if (Indexer.slaveServersShort != null && !Indexer.slaveServersShort.isEmpty()) {
            String[] ssus = Indexer.slaveServersShort.split(Indexer.split);
            if (ssus != null && ssus.length > 0) {
                for (String ssu : ssus) {
                    if (ssu != null && !ssu.isEmpty()) {
                        slavesShort.add(ssu);
                    }
                }
            }
        }
    }


    public static List<String> getSlavesEw() {
        return slavesEw;
    }

    public void setSlavesEw(List<String> slavesEw) {
        Indexer.slavesEw = slavesEw;
    }


    public static String getCoreServersEw() {
        return coreServersEw;
    }

    public void setCoreServersEw(String coreServersEw) {
        Indexer.coreServersEw = coreServersEw;
    }


    public static String getSlaveServersEw() {
        return slaveServersEw;
    }

    public void setSlaveServersEw(String slaveServersEw) {
        Indexer.slaveServersEw = slaveServersEw;
        Indexer.slavesEw = new ArrayList<String>();
        if (Indexer.slaveServersEw != null && !Indexer.slaveServersEw.isEmpty()) {
            String[] ssus = Indexer.slaveServersEw.split(Indexer.split);
            if (ssus != null && ssus.length > 0) {
                for (String ssu : ssus) {
                    if (ssu != null && !ssu.isEmpty()) {
                        slavesEw.add(ssu);
                    }
                }
            }
        }
    }


    public static List<String> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<String> slaves) {
        Indexer.slaves = slaves;
    }


    public static List<String> getSlavesAll() {
        return slavesAll;
    }

    public void setSlavesAll(List<String> slavesAll) {
        Indexer.slavesAll = slavesAll;
    }


    public static List<String> getSlavesShort() {
        return slavesShort;
    }

    public void setSlavesShort(List<String> slavesShort) {
        Indexer.slavesShort = slavesShort;
    }

    protected String getSearchSourceArrEsSmall1(String source) {
        String indexName = "";
        if (source.equals("0") || source.indexOf("0") == 0 || source.equals("")
                || source.equals("-1") || source.indexOf("-1") == 0) {
            return "news_small,blog_small,forum_threads_small,mblog_info_small,video_brief_small," +
                    "wechat_message_xigua_small,appdata_small";   //返回全部类型的
        } else {
            if (source.contains("1")) {
                indexName += "news_small,";
            }
            if (source.contains("2")) {
                indexName += "blog_small,";
            }
            if (source.contains("3")) {
                indexName += "forum_threads_small,";
            }
            if (source.contains("4")) {
                indexName += "mblog_info_small,";
            }
            if (source.contains("5")) {
                indexName += "video_brief_small,";
            }
            if (source.contains("6")) {
                indexName += "wechat_message_xigua_small,";
            }
            if (source.contains("7")) {
                indexName += "appdata_small,";
            }
            return indexName;
        }
    }

    public void test() {
        EsIndexSearch esIndexSearch = Indexer.getMonitoresSmallIndex(getSearchSourceArrEsSmall1("0"));

//		List<String[]> results = esIndexSearch.facetCountQueryOrderByAlphabet("it", -1);
//        List<String[]> results = esIndexSearch.facetCountQueryOrderByCount("it", 100, casia.isi.elasticsearch.common.SortOrder.DESC);
        String[] array = new String[]{"area_list"};
        Map<String,Long> results = esIndexSearch.facetCountQuerysOrderByCount(array);

//        esIndexSearch.outputResult(results);
    }

    public static void main(String[] args) {
        Indexer indexer = new Indexer();
        indexer.test();
    }

}

