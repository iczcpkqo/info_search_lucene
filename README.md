# Details of Accessing My Azure Instance

## 获得Trec_Eval生成的分数

* Step 1: **`进入项目根目录`**
> cd /opt/my_assignment_1/info_search_lucene

* Step 2: **`运行Shell脚本`**
> ./eval.sh

## 项目根目录

> cd /opt/my_assignment_1/info_search_lucene

## 程序文件`XiangMao.java`所在目录

> cd /opt/my_assignment_1/info_search_lucene/src/main/java

## 搜索结果文件

* 文件所在目录:

  > cd /opt/my_assignment_1/info_search_lucene/src/main/java

* 文件名:

  > my.record_for_search_results.txt

## 用于对比的结果文件QRelsCorrectedforTRECeval

* 文件所在目录:

  > c:\codedomain\workspace\modules\cs7is3-202122_info_search\info_search_lucene\src\main\java\corpus

* 文件名:
  
  > QRelsCorrectedforTRECeval

## 其他文件夹的功能解释

* `my_record` 文件夹
每次运行完程序后, 都会获得12份搜索结果, 这12种搜索结果对应了使用不同分析器和不同评分器的结果.
  - 分析器包括: Standard, Standard with stop words, simple
  - 评分器包括: BM25, Classic, LMD, Multi

* `qry_match_rel` 文件夹
将每次运行的结果都会和QRelsCorrectedforTRECeval文件进行对比, 并将结果保存在同一文件中.

---

github: git@github.com:iczcpkqo/info_search_lucene.git
