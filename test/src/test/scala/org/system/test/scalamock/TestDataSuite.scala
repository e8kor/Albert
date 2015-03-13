package org.system.test.scalamock

import org.scalamock.proxy.Mock

/**
 * Created by evgeniikorniichuk on 02/03/15.
 */
class TestDataSuite extends TestDataSpec {

 test("Simple Mock") {
   val mockWithExpectation:TestBehaviour with Mock = mock[TestBehaviour]
   (mockWithExpectation expects 'dummyField)() returns 5L
   (mockWithExpectation expects 'intDef)() returns 100
   (mockWithExpectation expects 'add1ToInt)() returns 1

   assert((mockWithExpectation dummyField) equals 5L)
   assert((mockWithExpectation intDef) equals 100)
   assert((mockWithExpectation add1ToInt) equals 1)
 }

}
