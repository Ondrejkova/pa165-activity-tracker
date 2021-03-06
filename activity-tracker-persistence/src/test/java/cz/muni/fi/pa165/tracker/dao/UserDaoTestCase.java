/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pa165.tracker.dao;

import cz.muni.fi.pa165.tracker.PersistenceApplicationContext;
import cz.muni.fi.pa165.tracker.entity.ActivityReport;
import cz.muni.fi.pa165.tracker.entity.SportActivity;
import cz.muni.fi.pa165.tracker.entity.Team;
import cz.muni.fi.pa165.tracker.entity.User;
import cz.muni.fi.pa165.tracker.enums.Sex;
import cz.muni.fi.pa165.tracker.enums.UserRole;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.inject.Inject;
import javax.validation.ValidationException;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Jan Grundmann
 * @version 28.10.2016
 */

@ContextConfiguration(classes =PersistenceApplicationContext.class)
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class UserDaoTestCase extends AbstractTestNGSpringContextTests{
    
    @Inject
    private UserDao userDao;
    
    @Inject
    private ActivityReportDao activityReportDao;
    
    @Inject
    private SportActivityDao sportActivityDao;
        
    private Team starousi;
    
    private SportActivity football;
    private SportActivity skiing;
       
    @BeforeMethod
    public void createSports() {
        football = new SportActivity();
        football.setName("Football");
        football.setCaloriesFactor(18.5);
        sportActivityDao.create(football);
        
        skiing = new SportActivity();
        skiing.setName("Skiing");
        skiing.setCaloriesFactor(88.8);
        sportActivityDao.create(skiing);
    }
    
    @Test
    public void testCreate(){
        createValidUser1();
    }
    
    @Test(expectedExceptions = DataAccessException.class)
    public void testCreateNull(){
        userDao.create(null);
    } 
    
    @Test(expectedExceptions = ValidationException.class)
    public void testCreateIncompleteUser(){
        User user = new User.Builder("petra@mail.com")
        .setFirstName("Petra")
        .setHeight(155)
        .setLastName("Nekompletní")
        .setRole(UserRole.REGULAR)
        .setSex(Sex.FEMALE)
        .setTeam(starousi)
        .setWeight(50)
        //.setDateOfBirth(LocalDate.ofYearDay(1980, 222))
        .build();
        userDao.create(user);
    }
    
    @Test
    public void testUpdate(){
        User user = createValidUser1();
        user.setLastName("NewLastName");
        user.addActivityReport(createSkiingActivity(user));
        user.addActivityReport(createFootballActivity(user));
        User updatedUser = userDao.update(user);
        assertDeepEquals(user, updatedUser);
    }
    
    @Test
    public void testFindById(){
        User user = createValidUser1();
        User expected = userDao.findById(user.getId());
        assertDeepEquals(user,expected);
    }
    
    @Test(expectedExceptions = DataAccessException.class)
    public void testFindUserNullId() {
        userDao.findById(null);
    }
    @Test
    public void testFindAll(){
        Assert.assertEquals(userDao.findAll().size(), 0);
        createValidUser1();
        Assert.assertEquals(userDao.findAll().size(), 1);
        createValidUser2();
        Assert.assertEquals(userDao.findAll().size(), 2);
    }
    
    @Test
    public void testFindByEmail(){
        User user1 = createValidUser1();
        User expected = userDao.findByEmail(user1.getEmail());
        assertDeepEquals(user1, expected);
    }
    
    @Test(expectedExceptions = DataAccessException.class)
    public void testFindUserNullEmail() {
        userDao.findByEmail(null);
    }
    
    @Test
    public void testRemove(){
        User user1 = createValidUser1();
        User user2 = createValidUser2();
        userDao.remove(user1);
        Assert.assertEquals(userDao.findAll().size(), 1);
        assertDeepEquals(userDao.findAll().get(0), user2);
    }
    
    @Test(expectedExceptions = DataAccessException.class)
    public void testRemoveNullUser() {
        userDao.remove(null);
    }
    
    @Test(expectedExceptions = DataAccessException.class)
    public void testRemoveNonExistingUser() {
        User user = new User();
        userDao.remove(user);
    }
    
    private User createValidUser1() {
        User user = new User.Builder("pepa@mail.com")
        .setPasswordHash("fakePasswordHash")
        .setFirstName("Pepa")
        .setHeight(150)
        .setLastName("Novy")
        .setRole(UserRole.REGULAR)
        .setSex(Sex.MALE)
        .setTeam(starousi)
        .setWeight(50)
        .setDateOfBirth(LocalDate.ofYearDay(1990, 333))
        .build();
        userDao.create(user);
        return user;
    }
    
    private User createValidUser2() {
        User user = new User.Builder("david@mail.com")
        .setPasswordHash("fakePasswordHash")
        .setFirstName("David")
        .setHeight(160)
        .setLastName("Unavený")
        .setRole(UserRole.REGULAR)
        .setSex(Sex.MALE)
        .setTeam(starousi)
        .setWeight(80)
        .setDateOfBirth(LocalDate.ofYearDay(1980, 222))
        .build();
        userDao.create(user);
        return user;
    }
    
    private ActivityReport createFootballActivity(User user) {
        ActivityReport footballReport = new ActivityReport();
        footballReport.setUser(user);
        footballReport.setBurnedCalories(888);
        footballReport.setStartTime(LocalDateTime.now().minusHours(2));
        footballReport.setEndTime(LocalDateTime.now().minusMinutes(5));
        footballReport.setSportActivity(football);
        activityReportDao.create(footballReport);
        return footballReport;
    }
    
    private ActivityReport createSkiingActivity(User user) {
        ActivityReport skiingReport = new ActivityReport();
        skiingReport.setUser(user);
        skiingReport.setBurnedCalories(2000);
        skiingReport.setStartTime(LocalDateTime.now().minusHours(5));
        skiingReport.setEndTime(LocalDateTime.now().minusMinutes(5));
        skiingReport.setSportActivity(skiing);
        activityReportDao.create(skiingReport);
        return skiingReport;
    }

    private void assertDeepEquals(User user1, User user2) {
        Assert.assertEquals(user1, user2);
        Assert.assertEquals(user1.getDateOfBirth(), user2.getDateOfBirth());
        Assert.assertEquals(user1.getPasswordHash(), user2.getPasswordHash());
        Assert.assertEquals(user1.getEmail(), user2.getEmail());
        Assert.assertEquals(user1.getFirstName(), user2.getFirstName());
        Assert.assertEquals(user1.getHeight(), user2.getHeight());
        Assert.assertEquals(user1.getId(), user2.getId());
        Assert.assertEquals(user1.getLastName(), user2.getLastName());
        Assert.assertEquals(user1.getRole(), user2.getRole());
        Assert.assertEquals(user1.getSex(), user2.getSex());
        Assert.assertEquals(user1.getTeam(), user2.getTeam());
        Assert.assertEquals(user1.getWeight(), user2.getWeight());       
    }
}

