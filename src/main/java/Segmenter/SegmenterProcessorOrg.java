package Segmenter;

import Organization.Organization;
import Utilities.StringNormalizer;
import VCGate.VCGate;
import vn.edu.vnu.uet.nlp.segmenter.UETSegmenter;

/*
update origin of orgs to server
 */
public class SegmenterProcessorOrg implements Runnable {
    private VCGate vcGate;
    private Organization organization;
    private UETSegmenter segmenter;

    public SegmenterProcessorOrg(VCGate vcGate, Organization organization, UETSegmenter segmenter) {
        this.vcGate = vcGate;
        this.organization = organization;
        this.segmenter = segmenter;
    }

    @Override
    public void run() {
        String segmentedName = segmenter.segment(this.organization.getName());
        String[] currentTokenizedWords = StringNormalizer.normalize(segmentedName).split(" ");

        try {
            Integer isVn = this.vcGate.isVnOrg(currentTokenizedWords);
            if(isVn == 1) {
                this.vcGate.updateOrganizationOrigin(this.organization.getId(), true);
            } else if(isVn == 0) {
                this.vcGate.updateOrganizationOrigin(this.organization.getId(), false);
            }
            System.out.println(isVn + " " + segmentedName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
