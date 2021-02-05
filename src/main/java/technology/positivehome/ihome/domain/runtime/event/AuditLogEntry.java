package technology.positivehome.ihome.domain.runtime.event;

import technology.positivehome.ihome.domain.constant.AuditLogAction;
import technology.positivehome.ihome.domain.constant.EntityType;
import technology.positivehome.ihome.domain.constant.ProcessorType;

import java.util.Date;

/**
 * Created by maxim on 6/25/19.
 **/
public class AuditLogEntry {

    private long id;
    private String sessionId;
    private String clientSessionId;
    private AuditLogAction action = AuditLogAction.UNDEFINED;
    private ProcessorType processorType = ProcessorType.UNDEFINED;
    private long processorId;
    private EntityType objType = EntityType.UNDEFINED;
    private long objId;
    private EntityType parentObjType = EntityType.UNDEFINED;
    private long parentObjId;
    private Date created = new Date();
    private String descr;
    private int statusCode = 1;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClientSessionId() {
        return clientSessionId;
    }

    public void setClientSessionId(String clientSessionId) {
        this.clientSessionId = clientSessionId;
    }

    public AuditLogAction getAction() {
        return action;
    }

    public void setAction(AuditLogAction action) {
        this.action = action;
    }

    public ProcessorType getProcessorType() {
        return processorType;
    }

    public void setProcessorType(ProcessorType processorType) {
        this.processorType = processorType;
    }

    public long getProcessorId() {
        return processorId;
    }

    public void setProcessorId(long processorId) {
        this.processorId = processorId;
    }

    public EntityType getObjType() {
        return objType;
    }

    public void setObjType(EntityType objType) {
        this.objType = objType;
    }

    public long getObjId() {
        return objId;
    }

    public void setObjId(long objId) {
        this.objId = objId;
    }

    public EntityType getParentObjType() {
        return parentObjType;
    }

    public void setParentObjType(EntityType parentObjType) {
        this.parentObjType = parentObjType;
    }

    public long getParentObjId() {
        return parentObjId;
    }

    public void setParentObjId(long parentObjId) {
        this.parentObjId = parentObjId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public static AuditLogEntry startupMessage() {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setAction(AuditLogAction.SYSTEM_STARTUP);
        entry.setProcessorType(ProcessorType.SYSTEM);
        return entry;
    }
}
