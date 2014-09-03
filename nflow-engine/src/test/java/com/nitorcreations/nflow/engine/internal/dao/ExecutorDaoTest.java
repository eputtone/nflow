package com.nitorcreations.nflow.engine.internal.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.threeten.bp.ZonedDateTime;


public class ExecutorDaoTest extends BaseDaoTest {
  @Inject
  ExecutorDao dao;

  @Test
  public void tickCausesDeadNodeRecoveryPeriodically() {
    ZonedDateTime firstNextUpdate = dao.getMaxWaitUntil();
    dao.tick();
    ZonedDateTime secondNextUpdate = dao.getMaxWaitUntil();
    assertNotEquals(firstNextUpdate, secondNextUpdate);
    dao.tick();
    assertEquals(secondNextUpdate, dao.getMaxWaitUntil());
  }
}
