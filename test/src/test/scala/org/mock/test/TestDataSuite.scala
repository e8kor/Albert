package org.mock.test

import org.scalamock.proxy.Mock
import org.scalamock.scalatest.proxy.MockFactory
import org.scalatest.FunSuite

/**
 * Created by evgeniikorniichuk on 02/03/15.
 */
class TestDataSuite extends FunSuite with MockFactory {

 test("Simple Mock") {
   withExpectations{
     val mockVal: TestBehaviour with Mock = mock[TestBehaviour]
     mockVal.expects('intDef)().returning(5)

     assert((mockVal intDef) equals 5)
   }
 }

}
