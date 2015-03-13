package org.mock.test

/**
 * Created by evgeniikorniichuk on 02/03/15.
 */
class TestDataSuite extends FunSuite with MockFactory {

 test("Simple Mock") {
   val mockedBehaviour = withExpectations{
     val mockVal: TestBehaviour with Mock = mock[TestBehaviour]
     mockVal.expects('intDef)().returning(5)

     assert((mockedBehaviour intDef) equals 5)
   }
 }

}
