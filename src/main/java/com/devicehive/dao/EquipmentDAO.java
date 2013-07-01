package com.devicehive.dao;

import com.devicehive.configuration.Constants;
import com.devicehive.model.DeviceClass;
import com.devicehive.model.Equipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

public class EquipmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(DeviceClassDAO.class);

    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT)
    private EntityManager em;

    @Transactional
    public Equipment findByCode(String code) {
        TypedQuery<Equipment> query = em.createNamedQuery("Equipment.findByCode", Equipment.class);
        query.setParameter("code", code);
        List<Equipment> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Transactional
    public void saveEquipment(Equipment... equipment) {
        for (Equipment e : equipment) {
            em.persist(e);
        }
    }

    @Transactional
    public void updateEquipment(Equipment... equipment) {
        try{
        for (Equipment e : equipment) {
            em.lock(equipment, LockModeType.PESSIMISTIC_WRITE);
            em.merge(equipment);
        }
        }
        catch (Exception e){
            logger.debug(e.getMessage(), e);
        }
    }

    @Transactional
    public List<Equipment> getByDeviceClass(DeviceClass deviceClass) {
        TypedQuery<Equipment> query = em.createNamedQuery("Equipment.getByDeviceClass", Equipment.class);
        query.setParameter("deviceClass", deviceClass);
        return query.getResultList();
    }

    @Transactional
    public void removeEquipment(Equipment... equipments) {
        for (Equipment equipment : equipments) {
            em.lock(equipment, LockModeType.PESSIMISTIC_WRITE);
            em.remove(equipment);
        }
    }
}
