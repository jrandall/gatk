package org.broadinstitute.sting.queue.pipeline

/*
 * Copyright (c) 2011, The Broad Institute
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

import org.testng.annotations.Test
import org.broadinstitute.sting.BaseTest

class PacbioProcessingPipelineTest {
  @Test(timeOut=36000000)
  def testPacbioProcessingPipeline {
    val testOut = "exampleBAM.recal.bam"
    val spec = new PipelineTestSpec
    spec.name = "pacbioProcessingPipeline"
    spec.args = Array(
      " -S public/scala/qscript/org/broadinstitute/sting/queue/qscripts/PacbioProcessingPipeline.scala",
      " -R " + BaseTest.publicTestDir + "exampleFASTA.fasta",
      " -i " + BaseTest.publicTestDir + "exampleBAM.bam",
      " -blasr ",
      " -test ",
      " -D " + BaseTest.publicTestDir + "exampleDBSNP.vcf").mkString
    spec.fileMD5s += testOut -> "b84f9c45e045685067ded681d5e6224c"
    PipelineTest.executeTest(spec)
  }
}
