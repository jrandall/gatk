package org.broadinstitute.sting.gatk.walkers.annotator;

import org.broadinstitute.sting.gatk.contexts.AlignmentContext;
import org.broadinstitute.sting.gatk.contexts.ReferenceContext;
import org.broadinstitute.sting.gatk.refdata.RefMetaDataTracker;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.AnnotatorCompatible;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.ExperimentalAnnotation;
import org.broadinstitute.sting.gatk.walkers.annotator.interfaces.GenotypeAnnotation;
import org.broadinstitute.sting.utils.genotyper.PerReadAlleleLikelihoodMap;
import org.broadinstitute.sting.utils.MathUtils;
import org.broadinstitute.sting.utils.codecs.vcf.VCFFormatHeaderLine;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeaderLineType;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * The allele balance (fraction of ref bases over ref + alt bases) separately for each bialleleic het-called sample
 */
public class AlleleBalanceBySample extends GenotypeAnnotation implements ExperimentalAnnotation {

    public void annotate(final RefMetaDataTracker tracker,
                         final AnnotatorCompatible walker,
                         final ReferenceContext ref,
                         final AlignmentContext stratifiedContext,
                         final VariantContext vc,
                         final Genotype g,
                         final GenotypeBuilder gb,
                         final PerReadAlleleLikelihoodMap alleleLikelihoodMap){
        if ( stratifiedContext == null )
            return;

        Double ratio = annotateSNP(stratifiedContext, vc, g);
        if (ratio == null)
            return;

        gb.attribute(getKeyNames().get(0), Double.valueOf(String.format("%.2f", ratio.doubleValue())));
    }

    private Double annotateSNP(AlignmentContext stratifiedContext, VariantContext vc, Genotype g) {
        double ratio = -1;

        if ( !vc.isSNP() )
            return null;

        if ( !vc.isBiallelic() )
            return null;

        if ( g == null || !g.isCalled() )
            return null;

        if (!g.isHet())
            return null;

        Collection<Allele> altAlleles = vc.getAlternateAlleles();
        if ( altAlleles.size() == 0 )
            return null;

        final String bases = new String(stratifiedContext.getBasePileup().getBases());
        if ( bases.length() == 0 )
            return null;
        char refChr = vc.getReference().toString().charAt(0);
        char altChr = vc.getAlternateAllele(0).toString().charAt(0);

        int refCount = MathUtils.countOccurrences(refChr, bases);
        int altCount = MathUtils.countOccurrences(altChr, bases);

        // sanity check
        if ( refCount + altCount == 0 )
            return null;

        ratio = ((double)refCount / (double)(refCount + altCount));
        return ratio;
    }

    public List<String> getKeyNames() { return Arrays.asList("AB"); }

    public List<VCFFormatHeaderLine> getDescriptions() { return Arrays.asList(new VCFFormatHeaderLine(getKeyNames().get(0), 1, VCFHeaderLineType.Float, "Allele balance for each het genotype")); }
}