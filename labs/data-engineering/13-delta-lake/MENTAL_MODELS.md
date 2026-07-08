# Mental Models for Delta Lake

## 1. Git for Data
Delta is like git for data lakes. Commit = write, Log = git log, Checkout = Time Travel, Branch = Clone, Merge = MERGE INTO, Revert = restore from version.

## 2. The Filing Cabinet
Transaction log = index card catalog. Each commit = new entries in catalog. VACUUM = shredding old cards no longer referenced. Files = documents in drawers.

## 3. The Layered Cake
Each version is a layer of cake. You can slice at any layer (Time Travel). OPTIMIZE is like pressing the cake into fewer, denser layers. VACUUM removes stale crumbs.
