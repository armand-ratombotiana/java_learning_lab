package com.java.reflection.annotations.lab05;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Exports;
import java.lang.module.ModuleDescriptor.Opens;
import java.util.Set;

public class ModuleReflectionLab {

    public static void main(String[] args) {
        Module thisModule = ModuleReflectionLab.class.getModule();
        System.out.println("Module name: " + thisModule.getName());
        System.out.println("Is named: " + thisModule.isNamed());

        ModuleDescriptor desc = thisModule.getDescriptor();
        if (desc != null) {
            System.out.println("  Requires: " + desc.requires());
            System.out.println("  Exports: ");
            for (Exports e : desc.exports()) {
                System.out.println("    " + e.source() + " -> " + e.targets());
            }
            System.out.println("  Opens: ");
            for (Opens o : desc.opens()) {
                System.out.println("    " + o.source() + " -> " + o.targets());
            }
            System.out.println("  Packages: " + desc.packages());
        }

        // List boot layer modules
        System.out.println("\nBoot layer modules (first 10):");
        ModuleLayer.boot().modules().stream()
                .map(Module::getName)
                .sorted()
                .limit(10)
                .forEach(name -> System.out.println("  " + name));

        // Check if a package is open
        String pkg = ModuleReflectionLab.class.getPackageName();
        System.out.println("\nPackage '" + pkg + "' is open? "
                + thisModule.isOpen(pkg));
    }
}
