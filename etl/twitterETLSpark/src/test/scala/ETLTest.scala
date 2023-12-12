import java.io.File
import edu.cmu.ETLScala
import org.apache.commons.io.FileUtils
import org.scalatest.{BeforeAndAfterEach, FunSpec, MustMatchers}

class ETLTest
  extends FunSpec
  with TestingUtil
  with MustMatchers
  with LocalSparkSession
  with BeforeAndAfterEach {

  val InputGraphPath = "data/test.txt"
  val OutputGraphPath = "data/Output"
  val ReferencePath = "data/test1.txt"

  /**
   * This function gets call before the execution of each test.
   * If adding tests of your own, make sure to delete your files as well.
   */
  override protected def beforeEach(): Unit = {
    super.beforeEach()
  }

  describe("the ETL process") {
    it("should match the expected output for test1") {
      ETLScala.dataETL(InputGraphPath, OutputGraphPath, spark)
      compareETL(OutputGraphPath, ReferencePath)
    }
  }

}
