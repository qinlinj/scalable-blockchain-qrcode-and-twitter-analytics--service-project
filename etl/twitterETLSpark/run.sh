
# ssh azureuser@twitter-elt-ssh.azurehdinsight.net

spark-submit --class edu.cmu.ETLScala \
--conf spark.sql.legacy.timeParserPolicy=LEGACY \
--conf spark.driver.memory=50g \
--conf spark.sql.broadcastTimeout=900 \
--conf spark.driver.maxResultSize=50g \
--conf spark.executor.memory=10g \
--num-executors 50 \
--conf spark.yarn.executor.memoryOverhead=3000 \
project_spark.jar \
wasb://datasets@clouddeveloper.blob.core.windows.net/twitter-dataset/

spark-submit --class edu.cmu.ETLScala \
--conf spark.sql.legacy.timeParserPolicy=LEGACY \
--conf spark.driver.memory=10g \
--conf spark.executor.cores=1 \
--num-executors 30 \
project_spark.jar \
wasb://datasets@clouddeveloper.blob.core.windows.net/twitter-dataset/part-r-00000.gz

s3://cmucc-datasets/twitter/dataset/part-r-00000.gz
wasb://datasets@clouddeveloper.blob.core.windows.net/twitter-dataset/part-r-00000.gz
