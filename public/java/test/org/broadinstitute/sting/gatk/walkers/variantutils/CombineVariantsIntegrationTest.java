/*
 * Copyright (c) 2010, The Broad Institute
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

package org.broadinstitute.sting.gatk.walkers.variantutils;

import org.broadinstitute.sting.WalkerTest;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Arrays;

/**
 * Tests CombineVariants
 */
public class CombineVariantsIntegrationTest extends WalkerTest {
    //
    // TODO TODO TODO TODO TODO TODO TODO TODO
    // TODO TODO TODO TODO TODO TODO TODO TODO
    //
    // TODO WHEN THE HC EMITS VALID VCF HEADERS ENABLE BCF AND REMOVE lenientVCFProcessing ARGUMENTS
    //
    // TODO TODO TODO TODO TODO TODO TODO TODO
    // TODO TODO TODO TODO TODO TODO TODO TODO
    // TODO TODO TODO TODO TODO TODO TODO TODO
    //
    private static String baseTestString(String args) {
        return "-T CombineVariants --no_cmdline_in_header -L 1:1-50,000,000 -o %s -R " + b36KGReference + args;
        //return "-T CombineVariants --no_cmdline_in_header -L 1:1-50,000,000 -o %s -U LENIENT_VCF_PROCESSING -R " + b36KGReference + args;
    }

    private void cvExecuteTest(final String name, final WalkerTestSpec spec, final boolean parallel) {
        spec.disableShadowBCF();
        if ( parallel )
            executeTestParallel(name, spec);
        else
            executeTest(name, spec);
    }

    public void test1InOut(String file, String md5) {
        test1InOut(file, md5, "");
    }

    public void test1InOut(String file, String md5, String args) {
         WalkerTestSpec spec = new WalkerTestSpec(
                 baseTestString(" -priority v1 -V:v1 " + validationDataLocation + file + args),
                 1,
                 Arrays.asList(md5));
         cvExecuteTest("testInOut1--" + file, spec, true);
    }

    public void combine2(String file1, String file2, String args, String md5) {
        combine2(file1, file2, args, md5, true);
    }

    public void combine2(String file1, String file2, String args, String md5, final boolean parallel) {
         WalkerTestSpec spec = new WalkerTestSpec(
                 baseTestString(" -priority v1,v2 -V:v1 " + validationDataLocation + file1 + " -V:v2 "+ validationDataLocation + file2 + args),
                 1,
                 Arrays.asList(md5));
         cvExecuteTest("combine2 1:" + new File(file1).getName() + " 2:" + new File(file2).getName(), spec, parallel);
    }

    public void combineSites(String args, String md5) {
        String file1 = "1000G_omni2.5.b37.sites.vcf";
        String file2 = "hapmap_3.3.b37.sites.vcf";
        WalkerTestSpec spec = new WalkerTestSpec(
                "-T CombineVariants --no_cmdline_in_header -o %s -R " + b37KGReference
                        + " -L 1:1-10,000,000 -V:omni " + validationDataLocation + file1
                        + " -V:hm3 " + validationDataLocation + file2 + args,
                1,
                Arrays.asList(md5));
        cvExecuteTest("combineSites 1:" + new File(file1).getName() + " 2:" + new File(file2).getName() + " args = " + args, spec, true);
    }

    public void combinePLs(String file1, String file2, String md5) {
         WalkerTestSpec spec = new WalkerTestSpec(
                 "-T CombineVariants --no_cmdline_in_header -o %s -R " + b36KGReference + " -priority v1,v2 -V:v1 " + privateTestDir + file1 + " -V:v2 " + privateTestDir + file2,
                 1,
                 Arrays.asList(md5));
         cvExecuteTest("combine PLs 1:" + new File(file1).getName() + " 2:" + new File(file2).getName(), spec, true);
    }

    @Test public void test1SNP() { test1InOut("pilot2.snps.vcf4.genotypes.vcf", "6469fce8a5cd5a0f77e5ac5d9e9e192b", " -U LENIENT_VCF_PROCESSING"); }
    @Test public void test2SNP() { test1InOut("pilot2.snps.vcf4.genotypes.vcf", "a4cedaa83d54e34cafc3ac4b80acf5b4", " -setKey foo -U LENIENT_VCF_PROCESSING"); }
    @Test public void test3SNP() { test1InOut("pilot2.snps.vcf4.genotypes.vcf", "ac58a5fde17661e2a19004ca954d9781", " -setKey null -U LENIENT_VCF_PROCESSING"); }
    @Test public void testOfficialCEUPilotCalls() { test1InOut("CEU.trio.2010_03.genotypes.vcf.gz", "67a8076e30b4bca0ea5acdc9cd26a4e0"); } // official project VCF files in tabix format

    @Test public void test1Indel1() { test1InOut("CEU.dindel.vcf4.trio.2010_06.indel.genotypes.vcf", "909c6dc74eeb5ab86f8e74073eb0c1d6"); }
    @Test public void test1Indel2() { test1InOut("CEU.dindel.vcf4.low_coverage.2010_06.indel.genotypes.vcf", "381875b3280ba56eef0152e56f64f68d"); }

    @Test public void combineWithPLs() { combinePLs("combine.3.vcf", "combine.4.vcf", "f0ce3fb83d4ad9ba402d7cb11cd000c3"); }

    @Test public void combineTrioCalls() { combine2("CEU.trio.2010_03.genotypes.vcf.gz", "YRI.trio.2010_03.genotypes.vcf.gz", "", "4efdf983918db822e4ac13d911509576"); } // official project VCF files in tabix format
    @Test public void combineTrioCallsMin() { combine2("CEU.trio.2010_03.genotypes.vcf.gz", "YRI.trio.2010_03.genotypes.vcf.gz", " -minimalVCF", "848d4408ee953053d2307cefebc6bd6d"); } // official project VCF files in tabix format
    @Test public void combine2Indels() { combine2("CEU.dindel.vcf4.trio.2010_06.indel.genotypes.vcf", "CEU.dindel.vcf4.low_coverage.2010_06.indel.genotypes.vcf", "", "629656bfef7713c23f3a593523503b2f"); }

    @Test public void combineSNPsAndIndels() { combine2("CEU.trio.2010_03.genotypes.vcf.gz", "CEU.dindel.vcf4.low_coverage.2010_06.indel.genotypes.vcf", "", "e54d0dcf14f90d5c8e58b45191dd0219"); }

    @Test public void uniqueSNPs() {
        // parallelism must be disabled because the input VCF is malformed (DB=0) and parallelism actually fixes this which breaks the md5s
        combine2("pilot2.snps.vcf4.genotypes.vcf", "yri.trio.gatk_glftrio.intersection.annotated.filtered.chr1.vcf", "", "e5ea6ac3905bd9eeea1a2ef5d2cb5af7", true);
    }

    @Test public void omniHM3Union() { combineSites(" -filteredRecordsMergeType KEEP_IF_ANY_UNFILTERED", "def52bcd3942bbe39cd7ebe845c4f206"); }
    @Test public void omniHM3Intersect() { combineSites(" -filteredRecordsMergeType KEEP_IF_ALL_UNFILTERED", "5f61145949180bf2a0cd342d8e064860"); }

    @Test public void threeWayWithRefs() {
        WalkerTestSpec spec = new WalkerTestSpec(
                baseTestString(" -V:NA19240_BGI "+validationDataLocation+"NA19240.BGI.RG.vcf" +
                        " -V:NA19240_ILLUMINA "+validationDataLocation+"NA19240.ILLUMINA.RG.vcf" +
                        " -V:NA19240_WUGSC "+validationDataLocation+"NA19240.WUGSC.RG.vcf" +
                        " -V:denovoInfo "+validationDataLocation+"yri_merged_validation_data_240610.annotated.b36.vcf" +
                        " -setKey centerSet" +
                        " -filteredRecordsMergeType KEEP_IF_ANY_UNFILTERED" +
                        " -U LENIENT_VCF_PROCESSING" +
                        " -priority NA19240_BGI,NA19240_ILLUMINA,NA19240_WUGSC,denovoInfo" +
                        " -genotypeMergeOptions UNIQUIFY -L 1"),
                1,
                Arrays.asList("58e6281df108c361e99673a501ee4749"));
        cvExecuteTest("threeWayWithRefs", spec, true);
    }

    // complex examples with filtering, indels, and multiple alleles
    public void combineComplexSites(String args, String md5) {
        String file1 = "combine.1.vcf";
        String file2 = "combine.2.vcf";
        WalkerTestSpec spec = new WalkerTestSpec(
                "-T CombineVariants --no_cmdline_in_header -o %s -R " + b37KGReference
                        + " -V:one " + privateTestDir + file1
                        + " -V:two " + privateTestDir + file2 + args,
                1,
                Arrays.asList(md5));
        cvExecuteTest("combineComplexSites 1:" + new File(file1).getName() + " 2:" + new File(file2).getName() + " args = " + args, spec, true);
    }

    @Test public void complexTestFull() { combineComplexSites("", "9d989053826ffe5bef7c4e05ac51bcca"); }
    @Test public void complexTestMinimal() { combineComplexSites(" -minimalVCF", "4f38d9fd30a7ae83e2a7dec265a28772"); }
    @Test public void complexTestSitesOnly() { combineComplexSites(" -sites_only", "46bbbbb8fc9ae6467a4f8fe35b8d7d14"); }
    @Test public void complexTestSitesOnlyMinimal() { combineComplexSites(" -sites_only -minimalVCF", "46bbbbb8fc9ae6467a4f8fe35b8d7d14"); }

    @Test
    public void combineDBSNPDuplicateSites() {
         WalkerTestSpec spec = new WalkerTestSpec(
                 "-T CombineVariants --no_cmdline_in_header -L 1:902000-903000 -o %s -R " + b37KGReference + " -V:v1 " + b37dbSNP132,
                 1,
                 Arrays.asList("aa926eae333208dc1f41fe69dc95d7a6"));
         cvExecuteTest("combineDBSNPDuplicateSites:", spec, true);
    }
}