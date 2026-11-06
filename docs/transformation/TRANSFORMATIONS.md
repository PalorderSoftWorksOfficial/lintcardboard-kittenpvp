# Mixins and bytecode transformations

## Scope

This document covers Fabric Mixin, ASM transformations, access wideners and class tweakers.

## Core constraint

A transformation that must alter a class before definition cannot be reliably installed after that class has already loaded. Establish transformation ordering for every target server bootstrap.

## Required work

- inspect the actual Mixin version and Fabric integration;
- establish a compatible transformation service;
- inspect classloader and mapping namespaces;
- validate target classes and methods against each server build;
- support access wideners and class tweakers only where verified;
- define deterministic transformation ordering;
- detect conflicts with server and other mod transformations;
- record transformation provenance and failure details.

Minecraft version and server fork both matter. A target that exists on Paper may be changed by Purpur or Leaf. Maintain platform-specific transformation profiles where needed.

## Failure policy

If a mandatory injection fails, fail the affected mod initialization or abort startup according to the severity policy. Never mark the mod fully loaded after skipping an essential transformation.

Diagnostics should include mod ID, configuration, target class, method/descriptor, injection point, platform/build and original exception.

## Enum extensions and custom registries

Investigate exact 26.2 loader mechanisms. Do not assume ordinary Mixin support also covers enum extensions, access transformations or dynamic registry behavior. Each mechanism needs its own test.

## Acceptance tests

A real controlled transformation must modify a test class before JVM definition through the actual target bootstrap. Test conflicting transformations, missing targets and platform-specific differences.