# Common Mistakes: EBS Upgrade and Migration

## Mistake 1: Ignoring Multi-Org Context

Code does not filter by operating unit. Fix by always setting FND_GLOBAL.ORG_ID.

## Mistake 2: Hard-Coding Profile Values

Profile values cached incorrectly. Use FND_PROFILE.GET at point of use.

## Mistake 3: Missing Edition Handling

Not checking edition name for EBR objects. Prefix calls with current edition.

## Mistake 4: Improper Exception Handling

Catching Exception instead of specific types. Use SQLException, IOException, etc.

## Mistake 5: Not Validating Dates

Oracle date arithmetic in Java without timezone. Use java.time.*.

## Mistake 6: Concurrent Program Hangs

Program does not set completion status. Always call completion_status.

## Mistake 7: Overlooking Audit

No audit trail for sensitive tables. Use FND_AUD procedures.

## Mistake 8: Incorrect Flexfield Context

Descriptive flexfield segments not populated. Ensure context column set.

## Mistake 9: Password Hard-Coding

Passwords in source code. Use Oracle Wallet.

## Mistake 10: Wrong File Permissions

EBS file system wrong ownership. Run adfixperms.pl.
