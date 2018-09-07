package com.company.rnd.scriptrepo.web.persistentscript;

import com.company.rnd.scriptrepo.entity.PersistentScript;
import com.company.rnd.scriptrepo.entity.PersistentScriptParameter;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;

import javax.inject.Inject;
import java.util.Collections;
import java.util.UUID;

public class PersistentScriptBrowse extends AbstractLookup {

    @Inject
    private Metadata metadata;

    @Inject
    private GroupDatasource<PersistentScript, UUID> persistentscriptsDs;

    @Inject
    private GroupDatasource<PersistentScriptParameter, UUID> persistentScriptParametersDs;

    public void onAddPropBtnClick() {
        PersistentScriptParameter parameter = metadata.create(PersistentScriptParameter.class);
        parameter.setPersistentScript(persistentscriptsDs.getItem());
        AbstractEditor editor = openEditor(parameter, WindowManager.OpenType.NEW_TAB, Collections.emptyMap(), persistentScriptParametersDs);
        editor.addCloseWithCommitListener(() -> {
            persistentScriptParametersDs.commit();
            persistentscriptsDs.commit();
        });
    }
}