package com.oracleebs.technical;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class OAFrameworkSimulation {
    public enum OAPageType { SEARCH, CREATE, UPDATE, SUMMARY }

    public static class OAViewObject {
        private final String voName;
        private final String entityName;
        private final String query;
        private final List<String> attributes;
        private final Map<String, Object> bindParameters;

        public OAViewObject(String name, String entity, String query) {
            this.voName = name;
            this.entityName = entity;
            this.query = query;
            this.attributes = new ArrayList<>();
            this.bindParameters = new ConcurrentHashMap<>();
        }

        public void addAttribute(String attr) { attributes.add(attr); }
        public void setBindParameter(String name, Object val) { bindParameters.put(name, val); }
        public String getVoName() { return voName; }
        public String getEntityName() { return entityName; }
        public String getQuery() { return query; }
        public List<String> getAttributes() { return Collections.unmodifiableList(attributes); }
        public Map<String, Object> getBindParameters() { return Collections.unmodifiableMap(bindParameters); }
    }

    public static class OAEntityObject {
        private final String entityName;
        private final String tableName;
        private final Map<String, String> attributeMapping;
        private boolean valid;

        public OAEntityObject(String name, String table) {
            this.entityName = name;
            this.tableName = table;
            this.attributeMapping = new LinkedHashMap<>();
            this.valid = true;
        }

        public void mapAttribute(String attr, String column) { attributeMapping.put(attr, column); }
        public String getEntityName() { return entityName; }
        public String getTableName() { return tableName; }
        public Map<String, String> getAttributeMapping() { return Collections.unmodifiableMap(attributeMapping); }
        public boolean isValid() { return valid; }
        public void setValid(boolean v) { valid = v; }
    }

    public static class OAApplicationModule {
        private final String amName;
        private final Map<String, OAViewObject> viewObjects;
        private final Map<String, OAEntityObject> entityObjects;

        public OAApplicationModule(String name) {
            this.amName = name;
            this.viewObjects = new ConcurrentHashMap<>();
            this.entityObjects = new ConcurrentHashMap<>();
        }

        public void addViewObject(OAViewObject vo) { viewObjects.put(vo.getVoName(), vo); }
        public void addEntityObject(OAEntityObject eo) { entityObjects.put(eo.getEntityName(), eo); }
        public String getAmName() { return amName; }
        public OAViewObject getViewObject(String name) { return viewObjects.get(name); }
        public OAEntityObject getEntityObject(String name) { return entityObjects.get(name); }
    }

    private final Map<String, OAApplicationModule> modules;

    public OAFrameworkSimulation() {
        this.modules = new ConcurrentHashMap<>();
    }

    public OAApplicationModule createModule(String name) {
        OAApplicationModule module = new OAApplicationModule(name);
        modules.put(name, module);
        return module;
    }

    public OAApplicationModule getModule(String name) {
        return modules.get(name);
    }

    public static OAFrameworkSimulation createDefault() {
        OAFrameworkSimulation sim = new OAFrameworkSimulation();
        var module = sim.createModule("ItemSearchAM");
        var vo = new OAViewObject("ItemSearchVO", "ItemEO", "SELECT * FROM MTL_SYSTEM_ITEMS WHERE 1=1");
        vo.addAttribute("itemId");
        vo.addAttribute("itemCode");
        vo.addAttribute("description");
        vo.setBindParameter("p_org_id", 101);
        module.addViewObject(vo);

        var eo = new OAEntityObject("ItemEO", "MTL_SYSTEM_ITEMS");
        eo.mapAttribute("itemId", "INVENTORY_ITEM_ID");
        eo.mapAttribute("itemCode", "SEGMENT1");
        eo.mapAttribute("description", "DESCRIPTION");
        module.addEntityObject(eo);
        return sim;
    }
}
