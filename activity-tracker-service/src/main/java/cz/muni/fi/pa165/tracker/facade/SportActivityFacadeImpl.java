/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pa165.tracker.facade;

import cz.muni.fi.pa165.tracker.dto.SportActivityCreateDTO;
import cz.muni.fi.pa165.tracker.dto.SportActivityDTO;
import cz.muni.fi.pa165.tracker.entity.SportActivity;
import cz.muni.fi.pa165.tracker.exception.NonExistingEntityException;
import cz.muni.fi.pa165.tracker.mapping.BeanMappingService;
import cz.muni.fi.pa165.tracker.service.SportActivityService;
import java.util.List;
import javax.inject.Inject;

/**
 * Implementation of (@link SportActivityFacade}
 *
 * @author Adam Laurenčík
 * @version 13.11.2016
 */
public class SportActivityFacadeImpl implements SportActivityFacade {

    @Inject
    private SportActivityService sportActivityService;

    @Inject
    private BeanMappingService beanMappingService;

    @Override
    public Long createSportActivity(SportActivityCreateDTO sportActivity) {
        if (sportActivity == null) {
            throw new IllegalArgumentException("sportActivity cannot be null");
        }
        SportActivity sportActivityEntity = beanMappingService.mapTo(sportActivity, SportActivity.class);
        sportActivityService.create(sportActivityEntity);
        return sportActivityEntity.getId();
    }

    @Override
    public void updateSportActivity(SportActivityDTO sportActivity) {
        if (sportActivity == null) {
            throw new IllegalArgumentException("sportActivity cannot be null");
        }
        SportActivity sportActivityEntity = beanMappingService.mapTo(sportActivity, SportActivity.class);
        sportActivityService.update(sportActivityEntity);
    }

    @Override
    public void removeSportActivity(Long sportActivityId) {
        SportActivity sportActivity = sportActivityService.findById(sportActivityId);
        if (sportActivity == null) {
            throw new NonExistingEntityException("Cannot remove not existing sportActivity");
        }
        sportActivityService.remove(sportActivity);
    }

    @Override
    public SportActivityDTO getSportActivityById(Long id) {
        SportActivity sportActivity = sportActivityService.findById(id);
        if (sportActivity == null) {
            throw new NonExistingEntityException("SportActivity for given id does not exists");
        }
        return beanMappingService.mapTo(sportActivity, SportActivityDTO.class);
    }

    @Override
    public SportActivityDTO getSportActivityByName(String name) {
        SportActivity sportActivity = sportActivityService.findByName(name);
        if (sportActivity == null) {
            throw new NonExistingEntityException("SportActivity for given name does not exists");
        }
        return beanMappingService.mapTo(sportActivity, SportActivityDTO.class);
    }

    @Override
    public List<SportActivityDTO> getAllSportActivities() {
        return beanMappingService.mapTo(sportActivityService.findAll(), SportActivityDTO.class);
    }

}
