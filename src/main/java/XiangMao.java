// TODO:
//       -[x]  1. 分字段建立索引
//       -[x]  2. 在指定字段中搜索
//       -[x]  3. 保存文件
//       -[x]  3. 格式化query文件搜索
//       -[x]  4. 使用query文件搜索
//       -[x]  4. 特殊字符替换
//       -[x]  4. 将搜索结果和目标结果对比
//       -[x]  5. 保存搜索评分
//       -[x]  6. 索引中增加全文字段
//       -[x]  7. 布尔查询解析器
//       -[x]  8. 关键词分析器
//       -[x]  9. 不同分析器
//       -[ ]  10. 剥离时间
//       -[ ]  11. 时间段搜索
//       -[x]  12. evil评价器
//       -[x]  13. UX
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
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
    public static void main(String[] args) throws IOException, ParseException {
//        ArrayList<HashMap<String, String>> tt =  indexStore.search("study of high-speed viscous flow past a two-dimensional", "parser", "id");
//        ArrayList<HashMap<String, String>> tt =  indexStore.searchPar("study of high-speed viscous flow past a two-dimensional", "content");
//        String[] aa = {"incompressible", "necessary", "compressible"};
//        ArrayList<HashMap<String, String>> tt =  indexStore.searchBool(aa, "content");

        Wrench.getMyPath();
/**
 *
 *      String baseDir = "src/main/java";
 *      LceOpera  indexStore = new LceOpera("index", "corpus", "cran.all.1400", "standard");
 *      indexStore.setUpStandardIndex();
 *      ArrayList<HashMap<String, String>> tt =  indexStore.searchPar(new String[]{"study of high-speed viscous flow past a two-dimensional"}, "content");

 *      for(HashMap<String, String> sd : tt)
 *          System.out.println(sd.get("id") + "|" + sd.get("author"));

 */

//        String[] analyzerTimes = {"standard", "standard_with_stop_words", "simple"};
//        String[] similarTimes = {"bm25", "classic", "lmd", "bool", "mul"};


        String[] analyzerTimes = {"standard"};
        String[] similarTimes = {"bm25"};


//        System.out.println("new test");
//        try {
//            String fil = "dd-2255.txt";
//            String fil2 = "/opt/my_assignment_1/info_search_lucene/src/main/java/xxs-2255.txt";
//
//            if(!Files.exists(Paths.get(fil)))
//                Files.createFile(Paths.get(fil));
//
//            if(!Files.exists(Paths.get(fil2)))
//                Files.createFile(Paths.get(fil2));
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }


        for(String tryAnalyzer : analyzerTimes) {
            for (String trySimilar: similarTimes) {
                long startTime; // = System.currentTimeMillis(); //获取开始时间
                long endTime; // = System.currentTimeMillis(); //获取结束时间

                // 设置基础目录
//                Wrench.workPath = "/opt/my_assignment_1/info_search_lucene/";
//                Wrench.proBasePath = "/opt/my_assignment_1/info_search_lucene/src/main/java";
                Wrench.workPath = "/opt/my_assignment_1/info_search_lucene/";
                Wrench.proBasePath = "/opt/my_assignment_1/info_search_lucene/src/main/java";

                // 获取查询数据
                Queries queries = new Queries(Wrench.proBasePath);
                ArrayList<HashMap<String, String>> scItems = queries.getQry();

                // 创建索引
                // time
                System.out.println("["+ tryAnalyzer +","+ trySimilar + "] Index being created...");
                startTime = System.currentTimeMillis(); //获取开始时间

                LceOpera indexStore = new LceOpera("index", "corpus", "cran.all.1400");

                switch (tryAnalyzer) {
                    case "standard_with_stop_words":
                        indexStore.setUpStandardIndexWithStopWords();
                        break;
                    case "simple":
                        indexStore.setUpSimpleIndex();
                        break;
                    default:
                        indexStore.setUpStandardIndex();
                        break;
                }


                // time
                endTime = System.currentTimeMillis(); //获取结束时间
                System.out.println("["+ tryAnalyzer +","+ trySimilar + "] Index creation completed, time consuming:" + (endTime - startTime) + "ms"); //输出程序运行时间

                //        indexStore.setUpStandardIndexWithStopWords();
                //        indexStore.setUpSimpleIndex();


                /* * */
                queries.getQueriesRelMap(queries.cranqrel);
                /* * */

                // 获取查询结果
                //        System.out.println("====================");
                //        System.out.println(scItems.get(0).get("query"));
                //        System.out.println("====================");

                // time
                System.out.println("["+ tryAnalyzer +","+ trySimilar + "] Start your search...");
                startTime = System.currentTimeMillis(); //获取开始时间

//                ArrayList<HashMap<String, String>> tt = indexStore.searchPar(new String[]{scItems.get(0).get("query")}, "content");


                // 1-5级 打印查询结果
                //        for(HashMap<String, String> sd : tt)
                //            System.out.println(sd.get("id") + " | " + sd.get("author") + " | " + sd.get("score"));

                getFileMatchQryRel(queries.getQry(), queries.getQueriesRelMap(queries.cranqrel), indexStore, tryAnalyzer, trySimilar);

                // time
                endTime = System.currentTimeMillis(); //获取结束时间
                System.out.println("["+ tryAnalyzer +","+ trySimilar + "] Search completed, time-consuming:" + (endTime - startTime) + "ms"); //输出程序运行时间

                // time
                System.out.println("["+ tryAnalyzer +","+ trySimilar + "] Search results are being saved...");
                startTime = System.currentTimeMillis(); //获取开始时间

                getRelForTrecEval(queries.getQry(), indexStore, tryAnalyzer, trySimilar);

                // time
                endTime = System.currentTimeMillis(); //获取结束时间
                System.out.println("["+ tryAnalyzer +","+ trySimilar + "] Search results have been saved, time-consuming:" + (endTime - startTime) + "ms"); //输出程序运行时间
            }
        }
    }


    public static void  getRelForTrecEval(ArrayList<HashMap<String, String>> query, LceOpera opera, String tryAnalyzer, String trySimilar) throws IOException, ParseException {
        System.out.println("---001");
        ArrayList<StringBuilder> relFileTrecEvalStr = new ArrayList<>();

        System.out.println("---002");
        for (HashMap<String, String> q : query) {
            StringBuilder pushTrecEvalStr = new StringBuilder();
            int rank = 0;
            ArrayList<HashMap<String, String>> scRelArr = opera.searchPar(new String[]{q.get("query")}, "total", trySimilar);

//            ArrayList<HashMap<String, String>> scRelArr = opera.searchBool(q.get("query").split(" "), "total");
            System.out.println("=== About the Search Results is: " + scRelArr.size());

            for (HashMap<String, String> hit : scRelArr) {
                System.out.println("---008");
                System.out.println("=== About the q keyset: " + q.keySet().toString());
                System.out.println("=== About the hit keyset: " + hit.keySet().toString());
                rank++;
                /* *
                 *  查询id: q.get("id")
                 *  固定Q0
                 *  文章id: hit.get("id")
                 *  文档排名: rank
                 *  我的得分: hit.get("score")
                 *  使用分析器: MULTI
                 */
                pushTrecEvalStr.append(q.get("id")).append(" ").
                        append("Q0").append(" ").
                        append(hit.get("id")).append(" ").
                        append(rank).append(" ").
                        append(hit.get("score")).append(" ").
                        append("STANDARD").append("\n");
            }
            System.out.println(pushTrecEvalStr.toString());
            relFileTrecEvalStr.add(pushTrecEvalStr);
        }
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        String dateStr = formatter.format(date);

        // 保存trec_eval所需要的文件
        Wrench.saveNew("", "my.record_" + tryAnalyzer + "_" + trySimilar  + "_" + dateStr + "", Wrench.proBasePath + "/my_record/");
        for(StringBuilder s : relFileTrecEvalStr) {
            Wrench.saveMore(s.toString(), "my.record_" + tryAnalyzer + "_" + trySimilar + "_" + dateStr + "", Wrench.proBasePath + "/my_record/");
        }

        System.out.println("---016");

        // 保存到上级目录, 只保存最好记录
        if(tryAnalyzer.equals("standard") && trySimilar.equals("bm25")) {
            Wrench.saveNew("", "my.record_for_search_results.txt", Wrench.proBasePath);
            for (StringBuilder s : relFileTrecEvalStr){
                Wrench.saveMore(s.toString(), "my.record_for_search_results.txt", Wrench.proBasePath);
            }
        }
    }


    /**
     * getFileMatchQryRel 将我的结果和搜索结果放在一起对比
     * @param query 查询文件
     * @param tarRel 需要对比的结果
     * @param opera 搜索类
     * @throws IOException IO
     * @throws ParseException IO
     */
    public static void getFileMatchQryRel(ArrayList<HashMap<String, String>> query, HashMap<Integer, HashMap<Integer, Integer>> tarRel, LceOpera opera, String tryAnalyzer, String trySimilar) throws IOException, ParseException {

        ArrayList<StringBuilder> relFileStr = new ArrayList<>();
        ArrayList<StringBuilder> relFileTrecEvalStr = new ArrayList<>();
        float sumSucceed = 0;
        float sumAll = 0;

        for (HashMap<String, String> q : query){
            StringBuilder pushStr = new StringBuilder();
            StringBuilder pushTrecEvalStr = new StringBuilder();
            int rank = 0;
            ArrayList<HashMap<String, String>> scRelArr = opera.searchPar(new String[]{q.get("query")}, "total", trySimilar);
            HashMap<Integer, HashMap<String, String>> scRel = Wrench.arrToHasMap(scRelArr);

            ///***
            for(Integer i : tarRel.get(Integer.valueOf(q.get("id"))).keySet()){
                rank++;
                /* *
                 *  查询id: q.get("id")
                 *  文章id: i
                 *  正确得分: tarRel.get(Integer.valueOf(q.get("id"))).get(i)
                 *  我的得分: scRel.get(i).get("score")
                 */
                sumAll++;
                pushStr.append(q.get("id")).append(",").
                        append(i).append(",").
                        append(tarRel.get(Integer.valueOf(q.get("id"))).get(i)).append(",");
                /* *
                 *  查询id: q.get("id")
                 *  固定Q0
                 *  文章id: i
                 *  文档排名: rank
                 *  我的得分: scRel.get(i).get("score")
                 *  使用分析器: MULTI
                 */
//                String ff = String.format("%.4f", tarRel.get(Integer.valueOf(q.get("id"))).get(i).floatValue());
                pushTrecEvalStr.append(q.get("id")).append(" ").
                        append("Q0").append(" ").
                        append(i).append(" ").
                        append(rank).append(" ");
//                        append(ff).append(" ");

                if (scRel.containsKey(i)) {
                    sumSucceed++;
                    pushStr.append(scRel.get(i).get("score")).append("\n");
//                    pushTrecEvalStr.append(String.format("%.6f", Integer.valueOf(scRel.get(i).get("score")).floatValue() )).append(" ").append("STANDARD").append("\n");
                    pushTrecEvalStr.append( scRel.get(i).get("score") ).append(" ").append("STANDARD").append("\n");
                }
                else {
                    pushStr.append(0).append("\n");
                    pushTrecEvalStr.append(0).append(" ").append("STANDARD").append("\n");
                }
            }

            relFileStr.add(pushStr);
            relFileTrecEvalStr.add(pushTrecEvalStr);
        }

        // 计算并存入覆盖率
        // System.out.println(relFileStr.add(new StringBuilder("覆盖率: " + String.format("%.0f",sumSucceed/sumAll*100) + "%")));

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
        String dateStr = formatter.format(date);

        // 保存数据进入CSV文件
        Wrench.saveNew("", "cranqrel.match_" + tryAnalyzer + "_" + trySimilar  + "_" + dateStr + ".csv",  Wrench.proBasePath + "/qry_match_rel/");
        for(StringBuilder s : relFileStr)
            Wrench.saveMore(s.toString(), "cranqrel.match_" + tryAnalyzer + "_" + trySimilar  + "_" + dateStr + ".csv", Wrench.proBasePath + "/qry_match_rel/");

        // 保存trec_eval所需要的文件
        Wrench.saveNew("", "match.record_" + tryAnalyzer + "_" + trySimilar  + "_" + dateStr + "", Wrench.proBasePath + "/qry_match_rel/");
        for(StringBuilder s : relFileTrecEvalStr)
            Wrench.saveMore(s.toString(), "match.record_" + tryAnalyzer + "_" + trySimilar  + "_" + dateStr + "", Wrench.proBasePath + "/qry_match_rel/");

    }
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
    public int maxResults = 1400;
    public CharArraySet stopWords;

    /**
     * LceOpera 初始化搜索类
     */
    public LceOpera(){

        // 初始化分析器
        this.ANALYZER_PICKER.put("standard", new StandardAnalyzer());
//        this.ANALYZER_PICKER.put("standard", new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET));

        // 初始化索引仓库
        this.indexPath = Wrench.proBasePath + "/index/";

        // 初始化文档仓库
        this.corpusPath = Wrench.proBasePath + "/corpus/";
        // 文档数据文件名
        this.corName = "cran.all.1400";
    }

    /**
     * LecOpera 初始化搜索类
     * @param indexPath 索引仓库, 基于程序文件
     * @param corpusPath 文档仓库, 基于程序文件
     * @param corpusFileName 文档名称, 一个
     */
//    public LceOpera(String indexPath, String corpusPath, String corpusFileName, String analyzerName){
    public LceOpera(String indexPath, String corpusPath, String corpusFileName){

        // 初始化停顿词
//        System.out.println(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        this.stopWords = new CharArraySet(0, true);
//        this.stopWords.addAll(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        this.stopWords.add("?");
        this.stopWords.add(",");
        this.stopWords.add(".");
        this.stopWords.add("/");
        this.stopWords.add("-");
        this.stopWords.add("(");
        this.stopWords.add(")");
        this.stopWords.add("=");
        this.stopWords.add("+");
        this.stopWords.add("\n");
        this.stopWords.add("\\n");
        this.stopWords.add("\r");
        this.stopWords.add("\\r");

        // 初始化分析器 选择组
        this.ANALYZER_PICKER.put("standard", new StandardAnalyzer());
        this.ANALYZER_PICKER.put("standard-with-stop-words", new StandardAnalyzer(this.stopWords));
        this.ANALYZER_PICKER.put("simple", new SimpleAnalyzer());
//        this.ANALYZER_PICKER.put("standard", new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET));
//        this.ANALYZER_PICKER.put("standard", new StandardAnalyzer(EnglishAnalyzer.getDefaultStopSet()));
//        System.out.println(EnglishAnalyzer.getDefaultStopSet());
//        System.out.println("------------------------------");

        // 初始化索引仓库
        this.indexPath = Wrench.proBasePath + "/" + indexPath + "/";

        // 初始化文档仓库
        this.corpusPath = Wrench.proBasePath + "/" + corpusPath + "/";
        // 文档数据文件名
        this.corName = corpusFileName;
    }

    /**
     * setUpStandardIndex 创建 使用标准分析器的索引
     * @throws IOException IO
     */
    public void setUpStandardIndex() throws IOException {
        createIndex(this.indexPath, this.corpusPath + this.corName, "standard", "ITABW");
    }

    /**
     * setUpStandardIndexWithStopWords 创建 使用有自定义停顿词的标准分析器的索引
     * @throws IOException IO
     */
    public void setUpStandardIndexWithStopWords() throws IOException {
        createIndex(this.indexPath, this.corpusPath + this.corName, "standard-with-stop-words", "ITABW");
    }

    /**
     * setUpStandardIndexWithStopWords 创建 使用简单分析器的索引
     * @throws IOException IO
     */
    public void setUpSimpleIndex() throws IOException {
        createIndex(this.indexPath, this.corpusPath + this.corName, "simple", "ITABW");
    }

    /**
     * createIndex 创建索引
     * @param indexPath 索引在工作路径地址
     * @param conPath 文档内容地址
     * @param analyzerName 分析器 ["standard", "standard_with_stop_words", "simple"]
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

        // 配置计分方法
//        this.writerConfig.setSimilarity(new TFIDFSimilarity());

        this.writerConfig.setSimilarity(new BM25Similarity());
//        this.writerConfig.setSimilarity(new LMDirichletSimilarity());
//        this.writerConfig.setSimilarity(new ClassicSimilarity());
//        this.writerConfig.setSimilarity(new BooleanSimilarity());
//        this.writerConfig.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity(), new BooleanSimilarity()}));
//        this.writerConfig.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity(), new LMDirichletSimilarity()}));
//        this.writerConfig.setSimilarity(new BooleanSimilarity());

        // 设置写入方式
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
//            System.out.println(articleMap.get("author"));
            Document doc = new Document();
            doc.add(new TextField("id", articleMap.get("id"), Field.Store.YES));
            doc.add(new TextField("title", articleMap.get("title"), Field.Store.YES));
            doc.add(new TextField("author", articleMap.get("author"), Field.Store.YES));
            doc.add(new TextField("publish", articleMap.get("publish"), Field.Store.YES));
            doc.add(new TextField("content", articleMap.get("content"), Field.Store.YES));
            doc.add(new TextField("total", articleMap.get("total"), Field.Store.YES));
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
        return search(sc, field, "parser", "bm25", maxResults);
    }
    /**
     * searchPar Parser模式搜索 **重载**
     * @param sc 需要搜索的内容
     * @param field 搜索相关文章的对应字段 ["id", "title","author", "publish", "content"]
     * @return 一个存放结果图的列表, 默认返回100条结果
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchPar(String[] sc, String field, String similar) throws IOException, ParseException {
        return search(sc, field, "parser", similar, this.maxResults);
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
        return search(sc, "content", "parser", "bm25", maxResults);
    }

    /**
     * searchPar Parser模式搜索 **重载**
     * @param sc 需要搜索的内容
     * @return 一个存放结果图的列表, 默认在 ”content“ 中进行搜索， 返回100条结果100
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchPar(String[] sc) throws IOException, ParseException {
        return search(sc, "content", "parser", "bm25",this.maxResults);
    }


    /**
     * searchBool Boolean模式搜索
     * @param sc 需要搜索的内容
     * @param field 搜索相关文章的对应字段 ["id", "title","author", "publish", "content"]
     * @return 一个存放结果图的列表
     * @throws IOException IO
     * @throws ParseException Parser
     */
    public ArrayList<HashMap<String, String>> searchBool(String[] sc, String field, String similar) throws IOException, ParseException {
        return search(sc, field, "boolean", similar,this.maxResults);
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
    public <E> ArrayList<HashMap<String, String>> search(E[] sc, String scField, String type, String similar, int maxResults) throws IOException, ParseException {


        System.out.println("in search sc is: " + sc[0]);
        System.out.println("in search sc.length is: " + sc.length);
        System.out.println("in search scField is: " + scField.toString());
        System.out.println("in search type is: " + type.toString());
        System.out.println("in search similar is: " + similar.toString());
        System.out.println("in search max is: " + maxResults);



        if(this.writer == null) {
            System.out.println("Please Set Up Index First!");
            return null;
        }

        Directory indexDir =  FSDirectory.open(Paths.get(this.indexPath));
        DirectoryReader iReader = DirectoryReader.open(indexDir);
        IndexSearcher iSearcher = new IndexSearcher(iReader);

//        iSearcher.setSimilarity(new ClassicSimilarity());
//        iSearcher.setSimilarity(new LMDirichletSimilarity());
//        iSearcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity()}));
//        iSearcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity(), new BooleanSimilarity()}));
//        iSearcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity(), new LMDirichletSimilarity()}));
//        iSearcher.setSimilarity(new BooleanSimilarity());

        switch (similar) {
            case "bm25":
                iSearcher.setSimilarity(new BM25Similarity());
                break;
            case "classic":
                iSearcher.setSimilarity(new ClassicSimilarity());
                break;
            case "lmd":
                iSearcher.setSimilarity(new LMDirichletSimilarity());
                break;
            case "bool":
                iSearcher.setSimilarity(new BooleanSimilarity());
                break;
            case "mul":
                iSearcher.setSimilarity(new MultiSimilarity(new Similarity[]{new BM25Similarity(), new ClassicSimilarity(), new LMDirichletSimilarity(), new BooleanSimilarity()}));
                break;
            default:
                iSearcher.setSimilarity(new BM25Similarity());
                break;
        }


        // 选择搜索方式
        Query query = null;
        if (Objects.equals(type, "parser")){
            QueryParser parser = new QueryParser(scField, this.analyzer);
            query = parser.parse((String) sc[0]);
        } else if (Objects.equals(type, "boolean")){
            BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            // 将sc中的内容全部拿出来创建查询条件并推入查询饵中
            for (E e: sc){
                Query term0 = new TermQuery(new Term(scField, e.toString()));
                queryBuilder.add(new BooleanClause(term0, BooleanClause.Occur.SHOULD));
            }
            query = queryBuilder.build();
        }


        System.out.println("=== Before do Search ===");
        ScoreDoc[] hits = iSearcher.search(query, maxResults).scoreDocs;
        System.out.println("=== Aefore do Search ===");
        System.out.println("number of hits[]" + hits.length);
        

//        System.out.println(hits.length);

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
            relMap.put("total", hitDoc.get("total"));
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
//        String[] relBox = article.split(".I [0-9]*\n");
//        String[] relBox = article.split(".I [0-9]*\n");
        String[] relBox = article.split(".I [0-9]*\r\n");
//        String[] relBox = article.split(".I 2");
//        System.out.println("Number of Result: " + relBox.length);
//        System.out.println("===" + relBox[1]);

        ArrayList<HashMap<String, String>> corArrMap = new ArrayList<>();

//        int sum = 0;
//        ArrayList<Integer> badEar = new ArrayList<>();

//        System.out.println(relBox.length);
        for(int i=1; i<relBox.length; i++){
            HashMap<String, String> oneArticle = new HashMap<String, String>();
            oneArticle.put("id", String.valueOf(i));

            String[] getTitle = relBox[i].split(".T(?=\r\n)|.A(?=\r\n)|.B(?=\r\n)|.W(?=\r\n)");
            oneArticle.put("title", getTitle[1].replaceAll("\n|\r",""));
            oneArticle.put("author", getTitle[2].replaceAll("\n|\r", ""));
            oneArticle.put("publish",getTitle[3].replaceAll("\n|\r", ""));

            StringBuilder conTail = new StringBuilder();
            for (int j=4; j<getTitle.length; j++)
                conTail.append(". ").append(getTitle[j]);
//            oneArticle.put("content", conTail.toString().replaceAll("\n|\r", "").replaceAll("[?]", " "));
            oneArticle.put("content", conTail.toString());
//            oneArticle.put("total", relBox[i].replaceAll("\\n|\\r|[?]", " "));
            oneArticle.put("total", relBox[i]);
//            System.out.println(oneArticle.get("total"));
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
        this.cranQry = new String(Files.readAllBytes(Paths.get(Wrench.proBasePath+"/corpus/cran.qry")));
        this.cranqrel = new String(Files.readAllBytes(Paths.get(Wrench.proBasePath +"/corpus/cranqrel")));
        this.TRECeval = new String(Files.readAllBytes(Paths.get(Wrench.proBasePath + "/corpus/QRelsCorrectedforTRECeval")));

        this.queries = txtConvert(this.cranQry, "cran.qry.new", Wrench.proBasePath + "/corpus/");
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

        System.out.println("===---001 Before save queries, basePath is: " + basePath);
//        System.out.println("=== Before save queries, this.cranQry is: " + this.cranQry);
        System.out.println("===---002 Before save queries, Wrench.proBasePath is: " + Wrench.proBasePath);
        this.queries = txtConvert(this.cranQry, "cran.qry.new", Wrench.proBasePath + "/corpus/");
        System.out.println("===---003 After save queries, this.queries is: " + this.queries.size());
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

//        System.out.println("=== Before save queries, this.cranQry is: " + this.cranQry);
//        System.out.println("=== Before save queries, Wrench.proBasePath is: " + Wrench.proBasePath);
        this.queries = txtConvert(this.cranQry, "cran.qry.new", Wrench.proBasePath + "/corpus/");
//        System.out.println("=== After save queries, this.queries is: " + this.queries);
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
        String[] txtSplitter = txt.split("\\.I\\s[0-9]{3}\\n\\.W\\n");
//        System.out.println("===---002.001");
//        System.out.println("After split Big TXT is : " + txt);
//        System.out.println("After split []size is : " + txtSplitter.length);


//        System.out.println("=== Split by \\r ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\r\\.W\\r").length);
//        System.out.println("=== Split by \\n ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\n\\.W\\n").length);
//        System.out.println("=== Split by \\rn ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\r\\n\\.W\\r\\n").length);
//        System.out.println("=== Split by \\nr ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\n\\r\\.W\\n\\r").length);
//
//
//        System.out.println("=== Split by \\\\r ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\\\r\\.W\\\\r").length);
//        System.out.println("=== Split by \\\\n ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\\\n\\.W\\\\n").length);
//        System.out.println("=== Split by \\\\rn ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\\\r\\\\n\\.W\\\\r\\\\n").length);
//        System.out.println("=== Split by \\\\nr ===");
//        System.out.println(txt.split("\\.I\\s[0-9]{3}\\\\n\\\\r\\.W\\\\n\\\\r").length);




        Wrench.saveNew("", fileName, savePath);
        for(int i=1; i<txtSplitter.length; i++){
            HashMap<String, String> q = new HashMap<String, String >();
            q.put("id", String.valueOf(i));
            q.put("query", txtSplitter[i].replaceAll("\r", "").replaceAll("[?]", " "));
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
    public static String workPath;
    public static String proBasePath;
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
//        System.out.println(Paths.get(file));
//        System.out.println(Files.exists(Paths.get(file)));
//        System.out.println("file is : " + file);
//        System.out.println("file path : " + Paths.get(file));

//         test
//        System.out.println("new test");
//        try {
//            String fil = "dds.txt";
//            String fil2 = "src/main/java/xxs.txt";
//
//            if(!Files.exists(Paths.get(fil)))
//                Files.createFile(Paths.get(fil));
//
//            if(!Files.exists(Paths.get(fil2)))
//                Files.createFile(Paths.get(fil2));
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }







//        String fil2 = "/opt/my_assignment_1/info_search_lucene/src/main/java/cran.qry.new";

//        System.out.println("fil2 file");
//        if(!Files.exists(Paths.get(fil2)))
//            Files.createFile(Paths.get(fil2));

        // try function
//        System.out.println("new test");
//        try {
//            String fil2 = "/opt/my_assignment_1/info_search_lucene/src/main/java/cran.qry.new";
//            String filnew = file.toString();
//
//            System.out.println("fil2 file");
//            if(!Files.exists(Paths.get(fil2)))
//                Files.createFile(Paths.get(fil2));
//
//            System.out.println("your file");
//            if(!Files.exists(Paths.get(filnew)))
//                Files.createFile(Paths.get(file));
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//
//        // ERROR
//        System.out.println("get in try");
//        try {
//            String fff = "/opt/my_assignment_1/info_search_lucene/src/main/java/corpus/tryyesssssss.txt";
//
//            System.out.println("000 tryyesssssssssss.txt");
//            Files.createFile(Paths.get("/opt/my_assignment_1/info_search_lucene/src/main/java/corpus/tryyes.txt"));
//
//            System.out.println("first tryyes.txt");
//            Files.createFile(Paths.get("/opt/my_assignment_1/info_search_lucene/src/main/java/corpus/tryyes.txt"));
//            System.out.println("Second no dot new.txt");
//            Files.createFile(Paths.get("/opt/my_assignment_1/info_search_lucene/src/main/java/corpus/cranqrynew.txt"));
//            System.out.println("Second no dot new.txt");
//            Files.createFile(Paths.get("/opt/my_assignment_1/info_search_lucene/src/main/java/corpus/cran.qry.new.txt"));
//            System.out.println("4 .new");
//            Files.createFile(Paths.get("/opt/my_assignment_1/info_search_lucene/src/main/java/corpus/cran.qry.new"));
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        System.out.println("Through Try");
//
//
//
//        System.out.println("try create without TRY");
//        Files.createFile(Paths.get(file));
//        System.out.println("SUCCESSFUL");


        if(!Files.exists(Paths.get(file)))
            Files.createFile(Paths.get(file));

        if (Objects.equals(type, "new")) {
            writer = Files.newBufferedWriter(Paths.get(file), StandardCharsets.UTF_8);
        }
        else if (Objects.equals(type, "more")) {
            writer = Files.newBufferedWriter(Paths.get(file), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        }

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

    /**
     * arrToHasMap 将一个列表转换为二维哈希
     * @param arr 需要转换的变量
     * @return 二维哈希
     */
    public static HashMap<Integer, HashMap<String, String>> arrToHasMap(ArrayList<HashMap<String, String>> arr){
        HashMap<Integer, HashMap<String, String>> conToHash = new HashMap<>();
        for(HashMap<String, String> hs : arr){
            conToHash.put(Integer.valueOf(hs.get("id")), hs);
        }
        return conToHash;
    }

    public static void getMyPath() throws IOException {
        File directory = new File("");//设定为当前文件夹
//            System.out.println(directory.getCanonicalPath());//获取标准的路径
//            System.out.println(directory.getAbsolutePath());//获取绝对路径
            System.out.println("Work Directory:"+System.getProperty("user.dir"));
    }

    // test
    public static void strTest(){

        System.out.println("new test");
        try {
            String fil = "ads.txt";
        if(!Files.exists(Paths.get(fil)))
            Files.createFile(Paths.get(fil));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
