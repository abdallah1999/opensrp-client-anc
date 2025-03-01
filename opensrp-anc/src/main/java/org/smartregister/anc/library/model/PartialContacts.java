package org.smartregister.anc.library.model;

import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.repository.PartialContactRepositoryHelper;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.List;
import java.util.Map;

public class PartialContacts {
    private Map<String, String> details;
    private String referral;
    private String baseEntityId;
    private boolean isFirst;
    private PartialContactRepositoryHelper partialContactRepositoryHelper;
    private List<PartialContact> partialContactList;

    public PartialContacts(Map<String, String> details, String referral, String baseEntityId, boolean isFirst) {
        this.details = details;
        this.referral = referral;
        this.baseEntityId = baseEntityId;
        this.isFirst = isFirst;
    }

    public PartialContactRepositoryHelper getPartialContactRepositoryHelper() {
        return partialContactRepositoryHelper;
    }

    public List<PartialContact> getPartialContactList() {
        return partialContactList;
    }

    public PartialContacts invoke() {
        partialContactRepositoryHelper = AncLibrary.getInstance().getPartialContactRepositoryHelper();

        if (partialContactRepositoryHelper != null) {
            if (isFirst) {
                partialContactList = partialContactRepositoryHelper.getPartialContacts(baseEntityId, 1);
            } else {
                if (referral != null) {
                    partialContactList = partialContactRepositoryHelper
                            .getPartialContacts(baseEntityId, Integer.valueOf(details.get(ConstantsUtils.REFERRAL)));
                } else {
                    partialContactList = partialContactRepositoryHelper.getPartialContacts(baseEntityId,
                            Integer.valueOf(details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
                }
            }
        } else {
            partialContactList = null;
        }
        return this;
    }
}

