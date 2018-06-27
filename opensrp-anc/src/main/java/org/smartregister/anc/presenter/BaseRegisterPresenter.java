package org.smartregister.anc.presenter;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.BaseRegisterContract;
import org.smartregister.anc.event.TriggerSyncEvent;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.interactor.BaseRegisterInteractor;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.anc.view.LocationPickerView;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by keyamn on 27/06/2018.
 */
public class BaseRegisterPresenter implements BaseRegisterContract.Presenter, BaseRegisterContract.InteractorCallBack {

    public static final String TAG = BaseRegisterPresenter.class.getName();

    private WeakReference<BaseRegisterContract.View> viewReference;
    private BaseRegisterContract.Interactor interactor;

    public BaseRegisterPresenter(BaseRegisterContract.View view) {
        viewReference = new WeakReference<>(view);
        interactor = new BaseRegisterInteractor();
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void availableLanguages() {
        List<String> availableLanguages = ConfigurableViewsLibrary.getJsonSpecHelper().getAvailableLanguages();
        getView().showLanguageDialog(availableLanguages);
    }

    @Override
    public void saveLanguage(String language) {
        Map<String, String> langs = AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
        Utils.saveLanguage(Utils.getKeyByValue(langs, language));

        getView().displayToast(language + " selected");
    }

    @Override
    public void logOutUser() {
        AncApplication.getInstance().logoutCurrentUser();
    }

    @Override
    public void triggerSync() {
        TriggerSyncEvent syncEvent = new TriggerSyncEvent();
        syncEvent.setManualSync(true);
        AncApplication.getInstance().triggerSync(syncEvent);

        getView().displaySyncNotification();
    }

    public void startForm(String formName, String entityId, String metadata, LocationPickerView locationPickerView) throws Exception {
        String currentLocationId = getlocationId(locationPickerView);
        startForm(formName, entityId, metadata, currentLocationId);
    }

    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this);
        }

        JSONObject form = JsonFormUtils.getFormAsJson(getView().getContext(), formName, entityId, currentLocationId);
        getView().startFormActivity(form);

    }

    private String getlocationId(LocationPickerView locationPickerView) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationPickerView.getSelectedItem());
    }

    @Override
    public void onNoUniqueId() {
        getView().displayShortToast(R.string.no_openmrs_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    @Override
    public BaseRegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy
    }

}
