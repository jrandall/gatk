package org.broadinstitute.sting.utils.genotype.vcf;

import org.broad.tribble.vcf.VCFHeader;
import org.broad.tribble.vcf.VCFHeaderLine;
import org.broad.tribble.vcf.VCFHeaderVersion;
import org.broadinstitute.sting.BaseTest;
import org.broadinstitute.sting.gatk.refdata.features.vcf4.VCF4Codec;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: aaron
 * Date: Jun 30, 2010
 * Time: 3:32:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class VCFHeaderUnitTest extends BaseTest {

    @Test
    public void testVCF4ToVCF3() {
        VCF4Codec codec = new VCF4Codec();
        List<String> headerFields = new ArrayList<String>();
        for (String str : VCF3_3headerStrings)
            headerFields.add(str);
        Assert.assertEquals(17,codec.createHeader(headerFields,"#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO"));
        codec.getHeader(VCFHeader.class).setVersion(VCFHeaderVersion.VCF3_3);
        checkMD5ofHeaderFile(codec, "5873e029bd50d6836b86438bccd15456");
    }

    @Test
    public void testVCF4ToVCF4() {
        VCF4Codec codec = new VCF4Codec();
        List<String> headerFields = new ArrayList<String>();
        for (String str : VCF3_3headerStrings)
            headerFields.add(str);
        Assert.assertEquals(17, codec.createHeader(headerFields, "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO"));
        checkMD5ofHeaderFile(codec, "4648aa1169257e0a8a9d30131adb5f35");
    }

    private void checkMD5ofHeaderFile(VCF4Codec codec, String md5sum) {
        File myTempFile = null;
        PrintWriter pw = null;
        try {
            myTempFile = File.createTempFile("VCFHeader","vcf");
            myTempFile.deleteOnExit();
            pw = new PrintWriter(myTempFile);
        } catch (IOException e) {
            Assert.fail("Unable to make a temp file!");
        }
        for (VCFHeaderLine line : codec.getHeader(VCFHeader.class).getMetaData())
            pw.println(line);
        pw.close();
        Assert.assertTrue(md5sum.equals(md5SumFile(myTempFile)));
    }




    public String[] VCF3_3headerStrings = {
                "##fileformat=VCFv4.0",
                "##filedate=2010-06-21",
                "##reference=NCBI36",
                "##INFO=<ID=GC, Number=0, Type=Flag, Description=\"Overlap with Gencode CCDS coding sequence\">",
                "##INFO=<ID=DP, Number=1, Type=Integer, Description=\"Total number of reads in haplotype window\">",
                "##INFO=<ID=AF, Number=1, Type=Float, Description=\"Dindel estimated population allele frequency\">",
                "##INFO=<ID=CA, Number=1, Type=String, Description=\"Pilot 1 callability mask\">",
                "##INFO=<ID=HP, Number=1, Type=Integer, Description=\"Reference homopolymer tract length\">",
                "##INFO=<ID=NS, Number=1, Type=Integer, Description=\"Number of samples with data\">",
                "##INFO=<ID=DB, Number=0, Type=Flag, Description=\"dbSNP membership build 129 - type match and indel sequence length match within 25 bp\">",
                "##INFO=<ID=NR, Number=1, Type=Integer, Description=\"Number of reads covering non-ref variant on reverse strand\">",
                "##INFO=<ID=NF, Number=1, Type=Integer, Description=\"Number of reads covering non-ref variant on forward strand\">",
                "##FILTER=<ID=NoQCALL, Description=\"Variant called by Dindel but not confirmed by QCALL\">",
                "##FORMAT=<ID=GT, Number=1, Type=String, Description=\"Genotype\">",
                "##FORMAT=<ID=HQ, Number=2, Type=Integer, Description=\"Haplotype quality\">",
                "##FORMAT=<ID=GQ, Number=1, Type=Integer, Description=\"Genotype quality\">",
                };
}
