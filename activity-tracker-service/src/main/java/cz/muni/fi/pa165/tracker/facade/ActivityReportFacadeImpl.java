package cz.muni.fi.pa165.tracker.facade;

import cz.muni.fi.pa165.tracker.dto.ActivityReportCreateDTO;
import cz.muni.fi.pa165.tracker.dto.ActivityReportDTO;
import cz.muni.fi.pa165.tracker.dto.ActivityReportUpdateDTO;
import cz.muni.fi.pa165.tracker.entity.ActivityReport;
import cz.muni.fi.pa165.tracker.entity.SportActivity;
import cz.muni.fi.pa165.tracker.entity.User;
import cz.muni.fi.pa165.tracker.exception.NonExistingEntityException;
import cz.muni.fi.pa165.tracker.mapping.BeanMappingService;
import cz.muni.fi.pa165.tracker.service.ActivityReportService;
import cz.muni.fi.pa165.tracker.service.SportActivityService;
import cz.muni.fi.pa165.tracker.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Implementation of {@link ActivityReportFacade}.
 *
 * @author Martin Styk
 * @version 07.11.2016
 */
@Service
@Transactional
public class ActivityReportFacadeImpl implements ActivityReportFacade {

    @Inject
    private ActivityReportService activityReportService;

    @Inject
    private BeanMappingService beanMappingService;

    @Inject
    private UserService userService;

    @Inject
    private SportActivityService sportService;

    @Override
    public Long createActivityReport(ActivityReportCreateDTO activityReportDTO) {
        if (activityReportDTO == null) {
            throw new IllegalArgumentException("activity report cannot be null");
        }
        ActivityReport activityReport = beanMappingService.mapTo(activityReportDTO, ActivityReport.class);

        activityReport.setUser(getExistingUserOrThrowException(activityReportDTO.getUserId()));
        activityReport.setSportActivity(getExistingSportOrThrowException(activityReportDTO.getSportActivityId()));

        activityReportService.create(activityReport);
        return activityReport.getId();
    }

    @Override
    public void updateActivityReport(ActivityReportUpdateDTO activityReportDTO) {
        if (activityReportDTO == null) {
            throw new IllegalArgumentException("activity report cannot be null");
        }
        ActivityReport activityReport = beanMappingService.mapTo(activityReportDTO, ActivityReport.class);

        if (activityReportService.findById(activityReport.getId()) == null) {
            throw new NonExistingEntityException("Can not update non existing activity report");
        }

        activityReport.setUser(getExistingUserOrThrowException(activityReportDTO.getUserId()));
        activityReport.setSportActivity(getExistingSportOrThrowException(activityReportDTO.getSportActivityId()));

        activityReportService.update(activityReport);
    }

    @Override
    public List<ActivityReportDTO> getAllActivityReports() {
        return beanMappingService.mapTo(activityReportService.findAll(), ActivityReportDTO.class);
    }

    @Override
    public ActivityReportDTO getActivityReportById(Long activityReportId) {
        ActivityReport activityReport = activityReportService.findById(activityReportId);
        if (activityReport == null) {
            throw new NonExistingEntityException("Activity report for doesn't exist");
        }
        return beanMappingService.mapTo(activityReport, ActivityReportDTO.class);
    }

    @Override
    public List<ActivityReportDTO> getActivityReportsByUser(Long userId) {
        User user = getExistingUserOrThrowException(userId);
        return beanMappingService.mapTo(activityReportService.findByUser(user), ActivityReportDTO.class);
    }

    @Override
    public List<ActivityReportDTO> getActivityReportsBySport(Long sportId) {
        SportActivity sportActivity = getExistingSportOrThrowException(sportId);
        return beanMappingService.mapTo(activityReportService.findBySport(sportActivity), ActivityReportDTO.class);
    }

    @Override
    public List<ActivityReportDTO> getActivityReportsByUserAndSport(Long userId, Long sportId) {
        User user = getExistingUserOrThrowException(userId);
        SportActivity activity = getExistingSportOrThrowException(sportId);

        return beanMappingService.mapTo(activityReportService.findByUserAndSport(user, activity),
                ActivityReportDTO.class);
    }

    @Override
    public void removeActivityReport(Long activityReportId) {
        ActivityReport activityReport = activityReportService.findById(activityReportId);
        if (activityReport == null) {
            throw new NonExistingEntityException("Can not remove not existing activity report");
        }
        activityReportService.remove(new ActivityReport(activityReportId));
    }

    @Override
    public void removeActivityReportsOfUser(Long userId) {
        User user = getExistingUserOrThrowException(userId);
        activityReportService.removeActivityReportsOfUser(user);
    }

    /**
     * @param id user id
     * @return existing user
     * @throws NonExistingEntityException if user does not exist
     */
    private User getExistingUserOrThrowException(Long id) {
        User user = userService.findById(id);
        if (user == null) {
            throw new NonExistingEntityException("User does not exist.");
        }
        return user;
    }

    /**
     * @param id sport id
     * @return existing sport
     * @throws NonExistingEntityException if sport does not exist
     */
    private SportActivity getExistingSportOrThrowException(Long id) {
        SportActivity sportActivity = sportService.findById(id);
        if (sportActivity == null) {
            throw new NonExistingEntityException("Sport does not exist.");
        }
        return sportActivity;
    }
}
