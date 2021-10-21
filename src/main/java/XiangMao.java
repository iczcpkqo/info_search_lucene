// TODO:
//       -[x]  1. 分字段建立索引
//       -[x]  2. 在指定字段中搜索
//       -[x]  3. 保存文件
//       -[x]  3. 格式化query文件搜索
//       -[ ]  4. 使用query文件搜索
//       -[ ]  4. 将搜索结果和目标结果对比
//       -[ ]  5. 保存搜索评分
//       -[ ]  6. 索引中增加全文字段
//       -[ ]  7. 布尔查询解析器
//       -[ ]  8. 关键词分析器
//       -[ ]  9. 不同分析器
//       -[ ]  10. 剥离时间
//       -[ ]  11. 时间段搜索
//       -[ ]  12. evil评价器
//       -[ ]  13. UX
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;


public class XiangMao {
    public static void  main(String[] args) throws IOException, ParseException {
//        ArrayList<HashMap<String, String>> tt =  indexStore.search("study of high-speed viscous flow past a two-dimensional", "parser", "id");
//        ArrayList<HashMap<String, String>> tt =  indexStore.searchPar("study of high-speed viscous flow past a two-dimensional", "content");
//        String[] aa = {"incompressible", "necessary", "compressible"};
//        ArrayList<HashMap<String, String>> tt =  indexStore.searchBool(aa, "content");

/**
 *
 *      String baseDir = "src/main/java";
 *      LceOpera  indexStore = new LceOpera("index", "corpus", "cran.all.1400", "standard");
 *      indexStore.setUpStandardIndex();
 *      ArrayList<HashMap<String, String>> tt =  indexStore.searchPar(new String[]{"study of high-speed viscous flow past a two-dimensional"}, "content");

 *      for(HashMap<String, String> sd : tt)
 *          System.out.println(sd.get("id") + "|" + sd.get("author"));

 */
        // 设置基础目录
        String baseDir = "src/main/java";

        // 获取查询数据
        Queries queries = new Queries(baseDir);
        ArrayList<HashMap<String, String>> scItems =  queries.getQry();

        // 创建索引
        LceOpera  indexStore = new LceOpera("index", "corpus", "cran.all.1400", "standard");
        indexStore.setUpStandardIndex();

        /* * */
            queries.getQueriesRelMap(queries.cranqrel);
        /* * */

        // 获取查询结果
        System.out.println("====================");
        System.out.println(scItems.get(0).get("query"));
        System.out.println("====================");
        ArrayList<HashMap<String, String>> tt =  indexStore.searchPar(new String[]{scItems.get(0).get("query")}, 2);

        // 打印查询结果
        for(HashMap<String, String> sd : tt)
            System.out.println(sd.get("id") + " | " + sd.get("author") + " | " + sd.get("score"));
    }

    // 获取与标准结果匹配的查询内容
    // 查询id, 文章id, 得分, 正确得分
//    public getFileMatchQrel(){
//
//    }

}

/**
 * 一种索引, 不同搜索, 一对多
 */
class LceOpera {

    public String corName;
    public String indexPath;
    public String corpusPath;
    public String content;
    public Analyzer analyzer;
    public Map<String, Analyzer> ANALYZER_PICKER = new HashMap<String, Analyzer>();
    public ArrayList<Document> documents = new ArrayList<Document>();
    public IndexWriterConfig writerConfig;
    public IndexWriter writer;
    public int maxResults = 100;

    /**
     * LceOpera 初始化搜索类
     */
    public LceOpera(){
        // 初始化分析器
        this.ANALYZER_PICKER.put("standard", new StandardAnalyzer());

        // 初始化索引仓库
        this.indexPath = "src/main/java" + "/index/";

        // 初始化文档仓库
        this.corpusPath = "src/main/java" + "/corpus/";
        // 文档数据文件名
        this.corName = "cran.all.1400";
    }

    /**
     * LecOpera 初始化搜索类
     * @param indexPath 索引仓库, 基于程序文件
     * @param corpusPath 文档仓库, 基于程序文件
     * @param corpusFileName 文档名称, 一个
     * @param analyzerName 分析器名称, 一个
     */
    public LceOpera(String indexPath, String corpusPath, String corpusFileName, String analyzerName){
        // 初始化分析器
        this.ANALYZER_PICKER.put(analyzerName, new StandardAnalyzer());

        // 初始化索引仓库
        this.indexPath = "src/main/java" + "/" + indexPath + "/";

        // 初始化文档仓库
        this.corpusPath = "src/main/java" + "/" + corpusPath + "/";
        // 文档数据文件名
        this.corName = corpusFileName;
    }

    /**
     * setUpStandardIndex 创建 标准索引
     * @throws IOException IO
     */
    public void setUpStandardIndex() throws IOException {
        createIndex(this.indexPath, this.corpusPath + this.corName, "standard", "ITABW");
    }

    /**
     * createIndex 创建索引
     * @param indexPath 索引在工作路径地址
     * @param conPath 文档内容地址
     * @param analyzerName 分析器 ["standard"]
     * @param textSplitModel 文本分割器 ["ITABW"]
     * @throws IOException IO
     */
    public void createIndex(String indexPath, String conPath, String analyzerName, String textSplitModel) throws IOException {

        // 接收要求
        this.indexPath = indexPath;
        this.corpusPath = conPath;

        // 获取索引保存目录
        FSDirectory indexDir = FSDirectory.open(Paths.get(this.indexPath));

        // 获取文章
        this.content = new String(Files.readAllBytes(Paths.get(this.corpusPath)));

        // 选择分析器
        if ("standard".equals(analyzerName))
            this.analyzer = this.ANALYZER_PICKER.get(analyzerName);
        else
            this.analyzer = this.ANALYZER_PICKER.get(analyzerName);

        // 舵手配置器
        this.writerConfig = new IndexWriterConfig((this.analyzer));
        this.writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // 创建舵手
        this.writer = new IndexWriter(indexDir, this.writerConfig);

        // 选择文本分割器
        ArrayList<HashMap<String, String >> conMap = new ArrayList<>();
        if (Objects.equals(textSplitModel, "ITABW"))
            conMap = washArticle(this.content);
        else
            conMap = washArticle(this.content);

        // 结构化所有文章
        ArrayList<Document> documents = new ArrayList<Document>();
        for (HashMap<String, String> articleMap : conMap){
            Document doc = new Document();
            doc.add(new TextField("id", articleMap.get("id"), Field.Store.YES));
            doc.add(new TextField("title", articleMap.get("title"), Field.Store.YES));
            doc.add(new TextField("author", articleMap.get("author"), Field.Store.YES));
            doc.add(new TextField("publish", articleMap.get("publish"), Field.Store.YES));
            doc.add(new TextField("content", articleMap.get("content"), Field.Store.YES));
            documents.add(doc);
        }

        // 文章写入
        this.writer.addDocuments(documents);

        // 工作完毕, 关闭
        this.writer.close();
        indexDir.close();
    }

    /**
     * searchPar Parser模式搜索
     * @param sc 需要搜索的内容
     * @param field 搜索相关文章的对应字段 ["id", "title","author", "publish", "content"]
     * @param maxResults 返回搜索结果的最大数量
     * @return 一个存放结果图的列表
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchPar(String[] sc, String field, int maxResults) throws IOException, ParseException {
        return search(sc, field, "parser", maxResults);
    }
    /**
     * searchPar Parser模式搜索 **重载**
     * @param sc 需要搜索的内容
     * @param field 搜索相关文章的对应字段 ["id", "title","author", "publish", "content"]
     * @return 一个存放结果图的列表, 默认返回100条结果
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchPar(String[] sc, String field) throws IOException, ParseException {
        return search(sc, field, "parser", 100);
    }
    /**
     * searchPar Parser模式搜索 **重载**
     * @param sc 需要搜索的内容
     * @param maxResults 返回搜索结果的最大数量
     * @return 一个存放结果图的列表, 默认在 “content”中搜索
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchPar(String[] sc, int maxResults) throws IOException, ParseException {
        return search(sc, "content", "parser", maxResults);
    }
    /**
     * searchPar Parser模式搜索 **重载**
     * @param sc 需要搜索的内容
     * @return 一个存放结果图的列表, 默认在 ”content“ 中进行搜索， 返回100条结果100
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchPar(String[] sc) throws IOException, ParseException {
        return search(sc, "total", "parser", 100);
    }


    /**
     * searchBool Boolean模式搜索
     * @param sc 需要搜索的内容
     * @param field 搜索相关文章的对应字段 ["id", "title","author", "publish", "content"]
     * @return 一个存放结果图的列表
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchBool(String[] sc, String field) throws IOException, ParseException {
        return search(sc, field, "boolean", 100);
    }

    /**
     * Search 搜索内容基础函数
     * @param sc 需要搜索的内容
     * @param scField 搜索相关文章的对应字段 ["id", "title","author", "publish", "content"]
     * @param type 选择解析器 ["parser", "boolean"]
     * @param maxResults 返回搜索结果的最大数量
     * @return 一个存放结果图的列表 [{"id", "title", "author", "publish", "content", "score"}]
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public <E> ArrayList<HashMap<String, String>> search(E[] sc, String scField, String type, int maxResults) throws IOException, ParseException {
        if(this.writer == null) {
            System.out.println("Please Set Up Index First!");
            return null;
        }

        Directory indexDir =  FSDirectory.open(Paths.get(this.indexPath));
        DirectoryReader iReader = DirectoryReader.open(indexDir);
        IndexSearcher iSearcher = new IndexSearcher(iReader);
        
        // 选择搜索方式
        Query query = null;
        if (Objects.equals(type, "parser")){
            QueryParser parser = new QueryParser(scField, this.analyzer);
            query = parser.parse((String) sc[0]);
        } else if (Objects.equals(type, "boolean")){
            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            // 将sc中的内容全部拿出来创建查询条件并推入查询饵中
            for (E e: sc){
                System.out.println(e);
                Query term0 = new TermQuery(new Term(scField, e.toString()));
                queryBuilder.add(new BooleanClause(term0, BooleanClause.Occur.MUST));
            }
            query = queryBuilder.build();
        }


        ScoreDoc[] hits = iSearcher.search(query, maxResults).scoreDocs;

        System.out.println(hits.length);

/**
 *
 *       for (int i=0; i< hits.length; i++){
 *           Document hitDoc = iSearcher.doc(hits[i].doc);
 *           System.out.println(i + ")" + hitDoc.get(fieldShow) + " | " + hits[i].score);
 *       }

 */


        ArrayList<HashMap<String, String>> hitsMap = new ArrayList<>();
        for (ScoreDoc hit : hits) {
            Document hitDoc = iSearcher.doc(hit.doc);
            HashMap<String, String> relMap = new HashMap<String, String >();
            relMap.put("id", hitDoc.get("id"));
            relMap.put("title", hitDoc.get("title"));
            relMap.put("author", hitDoc.get("author"));
            relMap.put("publish", hitDoc.get("publish"));
            relMap.put("content", hitDoc.get("content"));
            relMap.put("score", String.valueOf(hit.score));
            hitsMap.add(relMap);
        }

        iReader.close();
        indexDir.close();

        return hitsMap;
    }

    /**
     * washArticle 文本处理函数
     * @param byteArticle 需要处理的字符串
     * @return 存储结果的图列表
     */
    public ArrayList<HashMap<String, String>> washArticle(String byteArticle){
        String article = byteArticle;
        if(byteArticle == null)
            article = this.content;

//        Path path = Paths.get("C:/codedomain/workspace/java/demo/untitled17/src/article.txt");
//        String content = new String(Files.readAllBytes(path));
//        String content = ".Isdfsdsdfsdf.Ixxxxxx.Txxxxxxxx";
        // 第一项为数量
        String[] relBox = article.split(".I [0-9]*\n");

//        System.out.println("Number of Result: " + relBox.length);

        ArrayList<HashMap<String, String>> corArrMap = new ArrayList<>();

//        int sum = 0;
//        ArrayList<Integer> badEar = new ArrayList<>();

//        System.out.println(relBox.length);
        for(int i=1; i<relBox.length; i++){
            HashMap<String, String> oneArticle = new HashMap<String, String>();
            oneArticle.put("id", String.valueOf(i));

            String[] getTitle = relBox[i].split(".T(?=\n)|.A(?=\n)|.B(?=\n)|.W(?=\n)");
            oneArticle.put("title", getTitle[1].replaceAll("\n|\r",""));
            oneArticle.put("author", getTitle[2].replaceAll("\n|\r", ""));
            oneArticle.put("publish",getTitle[3].replaceAll("\n|\r", ""));

            StringBuilder conTail = new StringBuilder();
            for (int j=4; j<getTitle.length; j++)
                conTail.append(". ").append(getTitle[j]);
            oneArticle.put("content", conTail.toString().replaceAll("\n|\r", ""));

            corArrMap.add(oneArticle);

//            if (getTitle.length!=5) {
//                sum++;
//                badEar.add(i);
//            }
        }

//        System.out.println(corArrMap.toString());
//        System.out.println("====size corMap:" + corArrMap.size());
//        System.out.println("====sum=:" + sum);
//        System.out.println(badEar);
//        System.out.println("=============Customer=================");
//        System.out.println("id=" + corArrMap.get(0).get("id"));
//        System.out.println("title=" + corArrMap.get(0).get("title"));
//        System.out.println("author=" + corArrMap.get(0).get("author"));
//        System.out.println("publish=" + corArrMap.get(0).get("publish"));
//        System.out.println("content=" + corArrMap.get(0).get("content"));

        return corArrMap;
    }

}

// Scoring
class Scoring {

}

// Queries
class Queries {

    public String cranQry;
    public String cranqrel;
    public String TRECeval;
    public ArrayList<HashMap<String, String>> queries;
    // {queryId: {"articleId": "score"}}
    public HashMap<Integer, HashMap<Integer, Integer>> queriesRel = new HashMap<Integer, HashMap<Integer, Integer>>();

    /**
     * 使用默认数据源
     * @throws IOException IO
     */
    public Queries() throws IOException {
        this.cranQry = new String(Files.readAllBytes(Paths.get("src/main/java/corpus/cran.qry")));
        this.cranqrel = new String(Files.readAllBytes(Paths.get("src/main/java/corpus/cranqrel")));
        this.TRECeval = new String(Files.readAllBytes(Paths.get("src/main/java/corpus/QRelsCorrectedforTRECeval")));

        this.queries = txtConvert(this.cranQry, "cran.qry.new", "src/main/java/corpus/");
    }

    /**
     * 根据基准路径寻找数据, 不用再写完整地址了
     * @param basePath 基础路径地址
     * @throws IOException IO
     */
    public Queries(String basePath) throws IOException {
        this.cranQry = new String(Files.readAllBytes(Paths.get(basePath + "/corpus/cran.qry")));
        this.cranqrel = new String(Files.readAllBytes(Paths.get(basePath + "/corpus/cranqrel")));
        this.TRECeval = new String(Files.readAllBytes(Paths.get(basePath + "/corpus/QRelsCorrectedforTRECeval")));

        this.queries = txtConvert(this.cranQry, "cran.qry.new", "src/main/java/corpus/");
    }

    /**
     * 配置数据源
     * @param q 需要查询的文件地址
     * @param qrel 标准查询结果地址
     * @param tre 查询评分
     * @throws IOException IO
     */
    public Queries(String q, String qrel, String tre) throws IOException {
        this.cranQry = new String(Files.readAllBytes(Paths.get(q)));
        this.cranqrel = new String(Files.readAllBytes(Paths.get(qrel)));
        this.TRECeval = new String(Files.readAllBytes(Paths.get(tre)));

        this.queries = txtConvert(this.cranQry, "cran.qry.new", "src/main/java/corpus/");
    }

    /**
     * 重新组织查询文件 getQry
     * @return 保存文件内容的列表图
     * @throws IOException IO
     */
    public ArrayList<HashMap<String, String>> getQry() throws IOException {
        return this.queries;
    }

    /**
     * txtConvert 重新设置查询文件的序号
     * @param txt 查询文件的内容
     * @return 存储查询内容的列表图 [{"id","query"}]
     * @throws IOException IO
     */
    public ArrayList<HashMap<String, String>> txtConvert(String txt, String fileName, String savePath) throws IOException {
        ArrayList<HashMap<String, String>> txtBox = new ArrayList<>();
        String[] txtSplitter = txt.split("\\.I\\s[0-9]{3}\\r\\n\\.W\\r\\n");

        Wrench.saveNew("", fileName, savePath);
        for(int i=1; i<txtSplitter.length; i++){
            HashMap<String, String> q = new HashMap<String, String >();
            q.put("id", String.valueOf(i));
            q.put("query", txtSplitter[i].replaceAll("\r", ""));
            txtBox.add(q);
            Wrench.saveMore(".I " + q.get("id") + "\n.W\n" + q.get("query"), fileName, savePath);
        }
//        System.out.println(txtBox.get(0));
//        System.out.println(txtBox.get(224));
//        System.out.println(txtBox.size());
//
//        Wrench.saveMore("\nsf33434sf", "dsf.txt", "src/main/java/corpus/");
//        Wrench.saver(, "dsf.txt", "src/main/java/corpus/");

        return txtBox;
    }

    // convert the results Map
//    public HashMap<Integer, HashMap<String, String>> getQueriesRelMap(String txt){

    /**
     * getQueriesRelMap 将文件中的数据加载到变量中
     * @param txt 结果字符串
     */
    public HashMap<Integer, HashMap<Integer, Integer>> getQueriesRelMap(String txt){
        String[] txtSpliter = txt.split("\n");
        for(String str : txtSpliter){
            int[] nus = Wrench.splitToInt(str.replaceAll("\\s+", " "));
            if(!this.queriesRel.containsKey(nus[0])) {
                HashMap<Integer, Integer> sampleMap = new HashMap<Integer, Integer>();
                this.queriesRel.put(nus[0], sampleMap);
            }
            this.queriesRel.get(nus[0]).put(nus[1], nus[2]);
        }
        return this.queriesRel;
    }
}

// Draw
class Draw {

}

// Tool
class Wrench {
    /**
     * saveNew 写入新内容
     * @param saStr 需要存入的字符串
     * @param fileName 保存的文件名
     * @param path 文件保存位置
     * @throws IOException IO
     */
    public static void saveNew(String saStr, String fileName, String path) throws IOException {
        save(saStr, fileName, path, "new");
    }

    /**
     * saveNew 追加写入内容
     * @param saStr 需要存入的字符串
     * @param fileName 保存的文件名
     * @param path 文件保存位置
     * @throws IOException IO
     */
    public static void saveMore(String saStr, String fileName, String path) throws IOException {
        save(saStr, fileName, path, "more");
    }

    /**
     *
     * save 基本写入
     * @param saStr 需要存入的字符串
     * @param fileName 保存的文件名
     * @param path 文件保存位置
     * @param type 写入模式 ["new", "more"]
     * @throws IOException IO
     */
    public static void save(String saStr, String fileName, String path, String type) throws IOException {
        String file = path + fileName;
        BufferedWriter writer = null;
        if(!Files.exists(Paths.get(file)))
            Files.createFile(Paths.get(file));

        if (Objects.equals(type, "new"))
            writer = Files.newBufferedWriter(Paths.get(file), StandardCharsets.UTF_8);
        else if (Objects.equals(type, "more"))
            writer = Files.newBufferedWriter(Paths.get(file), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

        writer.write(saStr);
        writer.flush();
        writer.close();
    }

    /**
     * splitToInt 将一段存数字的字符串转换成浮点型, 默认按照空格切割"\\s"
     * @param txt 需要转换的字符串
     * @return 转换后的float数组
     */
    public static int[] splitToInt(String txt){
        return splitToInt(txt, "\\s");
    }

    /**
     * splitToInt 将一段存数字的字符串转换成浮点型
     * @param txt 需要转换的字符串
     * @param splitStr 数字之间是用什么切割的
     * @return 转换后的float数组
     */
    public static int[] splitToInt(String txt, String splitStr){
        String[] txtSpliter = txt.split(splitStr);
        int[] nus = new int[txtSpliter.length];
        for(int i=0; i<nus.length; i++)
            nus[i] = Integer.parseInt(txtSpliter[i]);
        return nus;
    }
}
