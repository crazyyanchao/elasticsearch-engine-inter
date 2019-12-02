package casia.isi.elasticsearch.util;
/**
 * 　　　　　　　 ┏┓       ┏┓+ +
 * 　　　　　　　┏┛┻━━━━━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　 ┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 █████━█████  ┃+
 * 　　　　　　　┃　　　　　　 ┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　 ┃ + +
 * 　　　　　　　┗━━┓　　　 ┏━┛
 * ┃　　  ┃
 * 　　　　　　　　　┃　　  ┃ + + + +
 * 　　　　　　　　　┃　　　┃　Code is far away from     bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ +
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　 ┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━━━┳┓┏┛ + + + +
 * 　　　　　　　　　 ┃┫┫　 ┃┫┫
 * 　　　　　　　　　 ┗┻┛　 ┗┻┛+ + + +
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author YanchaoMa yanchaoma@foxmail.com
 * @PACKAGE_NAME: casia.isiteam.util
 * @Description: TODO(File operate util)
 * @date 2018/1/18 11:49
 */
public class FileUtil {

    private final static Logger logger = Logger.getLogger(FileUtil.class);

    /**
     * @param
     * @return
     * @Description: TODO(Read one line)
     */
    public static String readOneLine(String filePath) {
        File file = new File(filePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String str = br.readLine();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Read all line)
     */
    public static String readAllLine(String filePath, String encoding) {
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(fileContent);

            return new String(fileContent, encoding);

        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support " + encoding);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param
     * @return
     * @Description: TODO(从指定行开始读取文件)
     */
    public static String readAllByLineNum(String file, int startLine) {
        StringBuilder sf = new StringBuilder();
        try {
            LineNumberReader lnr = new LineNumberReader(new FileReader(file));
            String buff;
            while ((buff = lnr.readLine()) != null) {
                if (lnr.getLineNumber() >= startLine) {
                    sf.append(buff);
                    sf.append("\r\n");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sf.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Save text)
     */
    public static boolean saveFile(String filePath, String content, boolean bool) {
        boolean bo = false;
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, bool), "UTF-8"));
            out.write(content);
            bo = true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bo;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Save text)
     */
    public static boolean saveFileNoCover(String filePath, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8"));
            out.write(content);
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Filter text)
     */
    public static String filterText(String content, String breakWordsPath) {
        String breakWordsPathStr = readAllLine(breakWordsPath, "UTF-8").trim();
        JSONArray arrBreakWords = JSONArray.parseArray(breakWordsPathStr);
        for (Object arrBreakWord : arrBreakWords) {
            content = content.replace((CharSequence) arrBreakWord, "");
        }
        return content;
    }

    /**
     * @param
     * @return
     * @Description: TODO(DOM4J XML writer)
     */
    public static void writerXml(List<String> elementList, String xmlFilePath, String two_word) {

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("words");
        Element book = root.addElement(two_word);

        int i = 0;
        for (String fragment : elementList) {
            // 为book节点添加子节点
            Element name = book.addElement("fragment");
            // 设置节点的值
            name.addAttribute("id", String.valueOf(i++));
            name.setText(fragment);
        }

        // 创建输出格式的对象，规定输出的格式为带换行和缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            // 创建输出对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(xmlFilePath)), format);
            // 设置输出，这里设置输出的内容不将特殊字符转义，例如<符号就输出<，如果不设置，系统默认会将特殊字符转义
            writer.setEscapeText(false);

            // 输出xml文件
            writer.write(document);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(DOM4J XML writer)
     */
    public static void writerXml(List<String> elementList, String xmlFilePath, String two_word, int i) {

        Document document = DocumentHelper.createDocument();
        Element root = document.addElement("words");
        Element book = root.addElement(two_word);
        for (String fragment : elementList) {
            // 为book节点添加子节点
            Element name = book.addElement("fragment");
            // 设置节点的值
            name.addAttribute("id", String.valueOf(i++));
            name.setText(fragment);
        }

        // 创建输出格式的对象，规定输出的格式为带换行和缩进的格式
        OutputFormat format = OutputFormat.createPrettyPrint();
        try {
            // 创建输出对象
            XMLWriter writer = new XMLWriter(new FileOutputStream(new File(xmlFilePath)), format);
            // 设置输出，这里设置输出的内容不将特殊字符转义，例如<符号就输出<，如果不设置，系统默认会将特殊字符转义
            writer.setEscapeText(false);

            // 输出xml文件
            writer.write(document);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(DOM4J XML read)
     */
    public static Set<String> readXML(String xmlFilePath, String nodeName) {
        Set<String> list = new HashSet<String>();
        File file = new File(xmlFilePath);
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(file);
            Element rootElement = document.getRootElement();
            Element data = rootElement.element(nodeName);
            Element element;
            String str;
            for (Iterator<Element> iterator = data.elementIterator("fragment"); iterator.hasNext(); ) {
                element = iterator.next();
                str = element.getText();
                if (str != null && !"".equals(str) && !list.contains(str)) {
                    list.add(str.trim());
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Does the string all in Chinese ?)
     */
    public static boolean isAllChinese(String string) {
        int n = 0;
        for (int i = 0; i < string.length(); i++) {
            n = (int) string.charAt(i);
            if (!(19968 <= n && n < 40869)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Load data from file direction)
     */
    public static List<List<String[]>> loadData(String filePath) {
        List<List<String[]>> reList = new ArrayList<List<String[]>>();

        File file = new File(filePath);
        if (file.isDirectory()) {
            List<String[]> list;
            String[] str;
            String[] fileList = file.list();
            for (String s : fileList) {
                File f = new File(filePath, s);
                list = new ArrayList<String[]>();
                str = readAllLine(filePath + File.separator + f.getName(), "UTF-8").split("\r\n");
                for (String s1 : str) {
                    String[] arr = s1.split(" ");
                    if ("".equals(arr[1])) {
                        list.add(new String[]{arr[0], arr[2]});
                    } else {
                        list.add(new String[]{arr[0], arr[1]});
                    }
                }
                reList.add(list);
            }
        }
        return reList;
    }

    /**
     * @param path txt file path
     * @return
     * @Description: TODO(Read txt)
     */
    public static Set<String> readFilterTxt(String path) {
        Set<String> set = new HashSet<String>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() > 1)
                    if (!set.contains(line)) {
                        set.add(line);
                    }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }

    /**
     * @param
     * @return
     * @Description: TODO(Get file size)
     */
    public static long getFileSzie(String filePath) {
        long fileSzie = 0;
        File file = new File(filePath);
        if (!file.isDirectory()) {
            System.out.println(file.isDirectory());
            System.out.println("Folder");
            System.out.println("Path = " + file.getPath());
            System.out.println("Name = " + file.getName());
            if (file.exists() && file.isFile()) {
                fileSzie = file.length();
                System.out.println("File " + file.getName() + " size is " + fileSzie);
            }
        } else if (file.isDirectory()) {
            System.out.println(file.isDirectory());
            String[] fileList = file.list();
            for (String s : fileList) {
                File f = new File(filePath, s);
                fileSzie += f.length();
            }
        }
        return fileSzie;
    }

    /**
     * @param @param ids
     * @param @param filename    参数
     * @return void    返回类型
     * @throws
     * @Title: writeIDSToFile
     * @Description: TODO(将ids写入文件)
     */
    public static void writeIDSToFile(String ids, String filename) {
        try {
            File dir = new File("cursor");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            FileWriter writer = new FileWriter(file, true);
            writer.write(ids + "\r\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(将数据写入CSV文件)
     */
    public static void writeDataToCSV(String data, String path, String fileName, boolean append) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, fileName);
            FileWriter writer = new FileWriter(file, append);
            writer.write(data);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(READ properties配置文件)
     */
    public static HashMap<String, Object> readProperties(String filePath) throws IOException {
        Properties properties = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream(filePath));
        properties.load(in);

        HashMap<String, Object> hashMap = new HashMap<>();
        // 获取配置文件NAME
        Enumeration enumeration = properties.propertyNames();

        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            Object value = properties.getProperty(key);
            hashMap.put(key, value);
        }
        return hashMap;
    }

    /**
     * @param @param ids
     * @param @param filename    参数 false覆盖 true追加
     * @return void    返回类型
     * @throws
     * @Title: writeIDSToFile
     * @Description: TODO(将ids写入文件)
     */
    public static void writeIDSToFile(String ids, String filename, boolean bool) {
        try {
            File dir = new File("cursor");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, filename);
            FileWriter writer = new FileWriter(file, bool);
            writer.write(ids + "\r\n");
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(去掉JSON数据的KEY的双引号)
     */
    public static String removeKeyDoubleQuotationMark(JSON json) {
        // DATA PACKAGE中属性KEY不能有双引号
        String dataPackage = null;
        if (json != null) {
            dataPackage = json.toJSONString().replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
        }
        return dataPackage;
    }


    /**
     * @param
     * @return
     * @Description: TODO(文件是否存在)
     */
    public static boolean isExistsFile(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * @param
     * @return
     * @Description: TODO(删除文件)
     */
    public static void deleteCurrentFile(String tempIdFile) {
        File file = new File(tempIdFile);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 从文件中读取最后处理的id
     *
     * @param lastIdFileName
     * @return
     */
    public static long getLastId(String lastIdFileName) {
        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File lastIdFile = new File(dir, lastIdFileName);
        if (!lastIdFile.getAbsoluteFile().exists()) {
            try {
                lastIdFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }
        FileReader reader = null;
        String id = null;
        try {
            reader = new FileReader(lastIdFile);
            char[] buffer = new char[12];
            int read = reader.read(buffer);
            if (read == -1) {
                id = "0";
            } else {
                char[] realBuffer = new char[read];
                System.arraycopy(buffer, 0, realBuffer, 0, read);
                id = new String(realBuffer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeReader(reader);
        }
        if (id != null && !"".equals(id)) {
            return Long.parseLong(id.trim());
        }
        return 0;
    }

    /**
     * 更新最新处理的id
     *
     * @param filaname
     * @param autoId
     */
    public static void updateCursor(String filaname, String autoId) {

        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filaname);
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(autoId);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeWriter(writer);
        }
    }

    /**
     * 游标文件是否存在
     *
     * @param filaname
     */
    public static boolean isExistsCursorIdsFile(String filaname) {

        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, filaname);
        return file.exists();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Read all line)
     */
    public static String readAllLineCursor(String filePath) {
        File dir = new File("cursor");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filePath);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];

        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            in.read(fileContent);

            return new String(fileContent);

        } catch (UnsupportedEncodingException e) {
            System.err.println("The OS does not support");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * @param writer:文件写入器
     * @return
     * @Description: TODO(关闭文件WRITER)
     */
    private static void closeWriter(BufferedWriter writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param reader:文件读取器
     * @return
     * @Description: TODO(关闭文件READER)
     */
    public static void closeReader(BufferedReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static String getFileContent(File file) {
        StringBuffer sb = new StringBuffer();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim()).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeReader(reader);
        }
        return sb.toString();
    }

    /**
     * 读取文件
     *
     * @param file
     * @return
     */
    public static Set<String> getFileContentByLine(File file) {
        Set<String> set = new HashSet<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                if ("".equals(line.trim()))
                    continue;
                set.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeReader(reader);
        }
        return set;
    }

    /**
     * 读取文件首行
     *
     * @param filePath
     * @return
     */
    public static String getFirstLine(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line;
            if ((line = reader.readLine()) != null) {
                return line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeReader(reader);
        }
        return null;
    }


    /**
     * 按行读取文件
     *
     * @param fileName 文件名
     * @return List<String> 行列表
     * @throws IOException
     */
    @SuppressWarnings("resource")
    public static List<String> readFileByLine(String fileName) throws IOException {
        List<String> lineList = new ArrayList<String>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), "UTF-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            lineList.add(line.trim());
        }

        return lineList;
    }

    /**
     * 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
     *
     * @param fileName 文件名
     * @param content  文件内容
     */
    public static void writeFileByAddNew(String fileName, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, true), "UTF-8"));
            out.write(content + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写一个新文件
     *
     * @param fileName 文件名
     * @param content  文件内容
     */
    public static void writeFileByNewFile(String fileName, String content) {
        BufferedWriter out = null;

        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, false), "UTF-8"));
            out.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param reader:文件读取器
     * @return
     * @Description: TODO(关闭文件READER)
     */
    public static void closeReader(FileReader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param
     * @return
     * @Description: TODO(BYTES转换)
     */
    public static String convertFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.00");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    /**
     * @param
     * @return
     * @Description: TODO(Test main entrance)
     */
    public static void main(String[] args) {
        FileUtil fileOperate = new FileUtil();
        //String filePath = "C:\\\\Users\\\\11416\\\\PycharmProjects\\\\TextSummary\\\\news.txt";
        String filePath = "data" + File.separator + "金庸小说全集\\神雕侠侣.txt";

        String breakWordsPath = "data" + File.separator + "news.txt";

        //System.out.println(fileOperate.readOneLine(filePath));
        //System.out.println(fileOperate.readOneLine(breakWordsPath));
        //String s = fileOperate.readAllLine(breakWordsPath, "UTF-8");
        //System.out.println(fileOperate.filterText(s));
        //System.out.println(fileOperate.filterText(s).length());

        //String filePathtt = "data" + File.separator + "Neologism" + File.separator+ "FilterBreakWords.txt"; // Save current filter content
        //
        //System.out.println(fileOperate.saveFile(filePathtt,"测试！！！！！！"));

        //String text = "“中央派来了一个沙瑞金（省委书记），又派来了一个田国富（纪委书记）”，这是《人民的名义》里的一个情节，以此说明推动从严治党的迫切性。\n";
        //System.out.println(new FileUtil().filterText(text));

        // Handle dictionaries
        //String s = fileOperate.readAllLine("data/Neologism/backup_dropword.dic", "UTF-8");
        //String[] arr = s.split("\r\n");
        //List<String> englishList = new ArrayList<String>();
        //List<String> twoWordList = new ArrayList<String>();
        //List<String> threeWordList = new ArrayList<String>();
        //List<String> fourWordList = new ArrayList<String>();
        //List<String> fiveWordList = new ArrayList<String>();
        //
        //for (int i = 0; i < arr.length; i++) {
        //    String sf = arr[i];
        //    if (fileOperate.isAllChinese(sf)) {
        //        if (arr[i].length() == 2) {
        //            twoWordList.add(arr[i]);
        //            fileOperate.writerXml(twoWordList, "data/Neologism/drop_word/two_word_fragments.xml", "two_word");
        //        } else if (arr[i].length() == 3) {
        //            threeWordList.add(arr[i]);
        //            fileOperate.writerXml(threeWordList, "data/Neologism/drop_word/three_word_fragments.xml", "three_word");
        //        } else if (arr[i].length() == 4) {
        //            fourWordList.add(arr[i]);
        //            fileOperate.writerXml(fourWordList, "data/Neologism/drop_word/four_word_fragments.xml", "four_word");
        //        } else if (arr[i].length() == 5) {
        //            fiveWordList.add(arr[i]);
        //            fileOperate.writerXml(fiveWordList, "data/Neologism/drop_word/five_word_fragments.xml", "five_word");
        //        }
        //    } else {
        //        englishList.add(arr[i]);
        //        fileOperate.writerXml(englishList, "data/Neologism/drop_word/english_fragments.xml", "english_word");
        //    }
        //}
        FileUtil operate = new FileUtil();
        //operate.loadData("data/");
        //String file = "data/neo4j_knowledge_graph/temp_test/bsid/filter_repetition/entities_relationship_trituple.txt";
        //String str = operate.readAllByLineNum(file, 140000);
    }

}

