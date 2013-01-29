package org.broadinstitute.sting.gatk.walkers.diagnostics;

import net.sf.samtools.SAMReadGroupRecord;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.gatk.CommandLineGATK;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.report.GATKReport;
import org.broadinstitute.sting.gatk.report.GATKReportTable;
import org.broadinstitute.sting.gatk.walkers.ReadWalker;
import org.broadinstitute.sting.utils.help.DocumentedGATKFeature;
import org.broadinstitute.sting.utils.sam.GATKSAMRecord;

import java.io.PrintStream;
import java.util.List;

/**
 * Outputs the read lengths of all the reads in a file.
 *
 *  <p>
 *     Generates a table with the read lengths categorized per sample. If the file has no sample information
 *     (no read groups) it considers all reads to come from the same sample.
 *  </p>
 *
 *
 * <h2>Input</h2>
 *  <p>
 *      A BAM file.
 *  </p>
 *
 * <h2>Output</h2>
 *  <p>
 *      A human/R readable table of tab separated values with one column per sample and one row per read.
 *  </p>
 *
 * <h2>Examples</h2>
 *  <pre>
 *    java
 *      -jar GenomeAnalysisTK.jar
 *      -T ReadLengthDistribution
 *      -I example.bam
 *      -R reference.fasta
 *      -o example.tbl
 *  </pre>
 *
 * @author Kiran Garimela
 */

@DocumentedGATKFeature( groupName = "Quality Control and Simple Analysis Tools", extraDocs = {CommandLineGATK.class} )
public class ReadLengthDistribution extends ReadWalker<Integer, Integer> {
    @Output
    public PrintStream out;

    private GATKReport report;

    public void initialize() {
        final List<SAMReadGroupRecord> readGroups = getToolkit().getSAMFileHeader().getReadGroups();

        report = new GATKReport();
        report.addTable("ReadLengthDistribution", "Table of read length distributions", 1 + (readGroups.isEmpty() ? 1 : readGroups.size()));
        GATKReportTable table = report.getTable("ReadLengthDistribution");

        table.addColumn("readLength");

        if (readGroups.isEmpty())
            table.addColumn("SINGLE_SAMPLE");
        else
            for (SAMReadGroupRecord rg : readGroups)
                table.addColumn(rg.getSample());
    }

    public boolean filter(ReferenceContext ref, GATKSAMRecord read) {
        return ( !read.getReadPairedFlag() || read.getReadPairedFlag() && read.getFirstOfPairFlag());
    }

    @Override
    public Integer map(ReferenceContext referenceContext, GATKSAMRecord samRecord, RefMetaDataTracker RefMetaDataTracker) {
        GATKReportTable table = report.getTable("ReadLengthDistribution");

        int length = Math.abs(samRecord.getReadLength());
        String sample = samRecord.getReadGroup().getSample();

        table.increment(length, sample);

        return null;
    }

    @Override
    public Integer reduceInit() {
        return null;
    }

    @Override
    public Integer reduce(Integer integer, Integer integer1) {
        return null;
    }

    public void onTraversalDone(Integer sum) {
        report.print(out);
    }
}
