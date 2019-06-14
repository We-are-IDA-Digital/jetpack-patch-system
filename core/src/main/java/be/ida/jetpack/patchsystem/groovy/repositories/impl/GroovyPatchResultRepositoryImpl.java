package be.ida.jetpack.patchsystem.groovy.repositories.impl;

import be.ida.jetpack.carve.manager.ModelManager;
import be.ida.jetpack.carve.manager.exception.ModelManagerException;
import be.ida.jetpack.patchsystem.JetpackConstants;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchFile;
import be.ida.jetpack.patchsystem.groovy.models.GroovyPatchResult;
import be.ida.jetpack.patchsystem.groovy.repositories.GroovyPatchResultRepository;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

@Component(
        name = "Jetpack - Groovy Patch Result Repository",
        service = GroovyPatchResultRepository.class,
        property = {
                Constants.SERVICE_DESCRIPTION + ":String=Repository for Patch Results (CRUD).",
                Constants.SERVICE_VENDOR + ":String=" + JetpackConstants.VENDOR,
        })
public class GroovyPatchResultRepositoryImpl implements GroovyPatchResultRepository {
    private final static Logger LOG = LoggerFactory.getLogger(GroovyPatchResultRepositoryImpl.class);

    private static final String RUNNING = "RUNNING";

    @Reference
    private ModelManager modelManager; //Carve

    @Override
    public GroovyPatchResult getResult(GroovyPatchFile patchFile) {
        GroovyPatchResult patchResult = null;

        try {
            patchResult = modelManager.retrieve(GroovyPatchResult.class, patchFile.getResultPath());
        } catch (ModelManagerException e) {
            LOG.error("Couldn't get GroovyPatchResult", e);
        }

        return patchResult;
    }

    @Override
    public GroovyPatchResult createResult(GroovyPatchFile patchFile) {
        GroovyPatchResult patchResult = new GroovyPatchResult(patchFile.getResultPath(), RUNNING, Calendar.getInstance());
        patchResult.setMd5(patchFile.getMd5());

        try {
            modelManager.persist(patchResult);
        } catch (ModelManagerException e) {
            LOG.error("Couldn't persist GroovyPatchResult", e);
        }
        return patchResult;
    }

    @Override
    public void updateResult(GroovyPatchResult patchResult) {
        try {
            patchResult.setEndDate(Calendar.getInstance());
            modelManager.persist(patchResult);
        } catch (ModelManagerException e) {
            LOG.error("Couldn't persist GroovyPatchResult", e);
        }
    }
}