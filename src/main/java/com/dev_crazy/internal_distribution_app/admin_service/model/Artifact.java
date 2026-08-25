package com.dev_crazy.internal_distribution_app.admin_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Artifact implements Comparable<Artifact>{
    private String resourceApplicationCode;
    private String artifactId;
    private String applicationCode;
    private String version;
    private Branch branch;
    private Platform platform;
    private Boolean enabled;
    private BinaryDetail binaryDetail;
    private Date created;
    private Date updated;

    public void generateResourceApplicationCode(){
        this.resourceApplicationCode = String.format("%s.%s.%s", this.applicationCode, this.platform.toString(),
                this.branch.toString());
    }

    public void generateArtifactId(){
        byte[] hashCode = DigestUtils.md5Hex(String.format("%s.%s",this.resourceApplicationCode, this.version))
                .getBytes();
        this.artifactId = UUID.nameUUIDFromBytes(hashCode).toString().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Artifact artifact)) {
            return false;
        }

        return this.resourceApplicationCode.equals(artifact.getResourceApplicationCode())
                && this.compareTo(artifact) == 0;
    }

    @Override
    public int compareTo(Artifact artifact) {
        int comparisonResult = 0;

        String[] version1Splits = this.version.split("\\.");
        String[] version2Splits = artifact.getVersion().split("\\.");
        int maxLengthOfVersionSplits = Math.max(version1Splits.length, version2Splits.length);

        for (int i = 0; i < maxLengthOfVersionSplits; i++){
            Integer v1 = i < version1Splits.length ? Integer.parseInt(version1Splits[i]) : 0;
            Integer v2 = i < version2Splits.length ? Integer.parseInt(version2Splits[i]) : 0;
            int compare = v1.compareTo(v2);
            if (compare != 0) {
                comparisonResult = compare;
                break;
            }
        }
        return comparisonResult;
    }
}
