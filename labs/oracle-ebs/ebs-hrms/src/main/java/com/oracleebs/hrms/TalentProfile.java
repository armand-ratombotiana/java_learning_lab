package com.oracleebs.hrms;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TalentProfile {
    public enum SkillLevel { BEGINNER, INTERMEDIATE, ADVANCED, EXPERT }

    public static class Skill {
        private final String name;
        private final String category;
        private final SkillLevel level;

        public Skill(String name, String category, SkillLevel level) {
            this.name = name;
            this.category = category;
            this.level = level;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public SkillLevel getLevel() { return level; }
    }

    public static class EmployeeProfile {
        private final String employeeId;
        private final String fullName;
        private final String department;
        private final String jobTitle;
        private final List<Skill> skills;
        private final List<String> certifications;
        private final Map<String, String> performanceRatings;

        public EmployeeProfile(String empId, String name, String dept, String title) {
            this.employeeId = empId;
            this.fullName = name;
            this.department = dept;
            this.jobTitle = title;
            this.skills = new ArrayList<>();
            this.certifications = new ArrayList<>();
            this.performanceRatings = new LinkedHashMap<>();
        }

        public void addSkill(Skill s) { skills.add(s); }
        public void addCertification(String cert) { certifications.add(cert); }
        public void addRating(String year, String rating) { performanceRatings.put(year, rating); }
        public String getEmployeeId() { return employeeId; }
        public String getFullName() { return fullName; }
        public String getDepartment() { return department; }
        public String getJobTitle() { return jobTitle; }
        public List<Skill> getSkills() { return Collections.unmodifiableList(skills); }
        public List<String> getCertifications() { return Collections.unmodifiableList(certifications); }
        public Map<String, String> getPerformanceRatings() { return Collections.unmodifiableMap(performanceRatings); }
    }

    public static class JobRequirement {
        private final String jobTitle;
        private final List<String> requiredSkills;
        private final List<String> preferredSkills;
        private final int minExperienceYears;

        public JobRequirement(String title, List<String> req, List<String> pref, int exp) {
            this.jobTitle = title;
            this.requiredSkills = req;
            this.preferredSkills = pref;
            this.minExperienceYears = exp;
        }

        public String getJobTitle() { return jobTitle; }
        public List<String> getRequiredSkills() { return requiredSkills; }
        public List<String> getPreferredSkills() { return preferredSkills; }
        public int getMinExperienceYears() { return minExperienceYears; }
    }

    private final Map<String, EmployeeProfile> profiles;
    private final Map<String, JobRequirement> jobRequirements;

    public TalentProfile() {
        this.profiles = new ConcurrentHashMap<>();
        this.jobRequirements = new ConcurrentHashMap<>();
    }

    public void addProfile(EmployeeProfile profile) {
        profiles.put(profile.getEmployeeId(), profile);
    }

    public void addJobRequirement(JobRequirement req) {
        jobRequirements.put(req.getJobTitle(), req);
    }

    public double calculateMatchScore(String employeeId, String jobTitle) {
        EmployeeProfile profile = profiles.get(employeeId);
        JobRequirement req = jobRequirements.get(jobTitle);
        if (profile == null || req == null) return 0.0;

        Set<String> profileSkills = new HashSet<>();
        for (Skill s : profile.getSkills()) {
            profileSkills.add(s.getName().toLowerCase());
        }

        long matchedRequired = req.getRequiredSkills().stream()
            .filter(s -> profileSkills.contains(s.toLowerCase())).count();
        long matchedPreferred = req.getPreferredSkills().stream()
            .filter(s -> profileSkills.contains(s.toLowerCase())).count();

        double reqScore = req.getRequiredSkills().isEmpty() ? 1.0 :
            (double) matchedRequired / req.getRequiredSkills().size();
        double prefScore = req.getPreferredSkills().isEmpty() ? 1.0 :
            (double) matchedPreferred / req.getPreferredSkills().size();

        return reqScore * 0.7 + prefScore * 0.3;
    }

    public static TalentProfile createDefault() {
        TalentProfile tp = new TalentProfile();
        var emp = new EmployeeProfile("E001", "Alice Smith", "Engineering", "Senior Developer");
        emp.addSkill(new Skill("Java", "Programming", SkillLevel.EXPERT));
        emp.addSkill(new Skill("SQL", "Database", SkillLevel.ADVANCED));
        emp.addSkill(new Skill("OAF", "EBS", SkillLevel.INTERMEDIATE));
        emp.addCertification("OCP Java SE 21");
        emp.addRating("2025", "Exceeds Expectations");
        tp.addProfile(emp);
        tp.addJobRequirement(new JobRequirement("Senior Developer",
            List.of("Java", "SQL"), List.of("OAF", "Spring"), 5));
        tp.addJobRequirement(new JobRequirement("Technical Architect",
            List.of("Java", "OAF", "SQL"), List.of("Cloud", "SOA"), 8));
        return tp;
    }
}
