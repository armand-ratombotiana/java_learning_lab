# Mock Interview: Design an Agent System for Automated Code Review

## Scenario
You are interviewing for a senior AI engineer role at a DevOps startup. They want an LLM-powered agent that autonomously reviews pull requests.

## Interviewer Opening Question
"Design an agent system that takes a PR diff and produces a comprehensive code review with actionable feedback."

## Candidate Response
"I'd build a multi-agent system with three specialized agents: a Static Analysis Agent for linting/type errors, a Logic Agent for semantic bugs, and a Style Agent for best practices. A Supervisor Agent orchestrates them and synthesizes the final review."

## Interviewer Probing Questions

**Q: How does the agent handle large diffs (1000+ lines)?**
"I'd diff-chunk: send each file to the relevant agent in parallel, then aggregate. Use map-reduce pattern — one pass per file, then a summarization pass."

**Q: How do you prevent hallucinations about non-existent bugs?**
"Ground every comment in specific line numbers. Use the diff context as strict grounding — if the model can't cite a line, the comment is discarded. Also add a validation step that re-checks suggestions against the actual code."

**Q: How do you learn from developer feedback?**
"Store review outcomes (accepted/rejected comments) in a feedback database. Use rejected comments as few-shot examples of what not to flag, and update the system prompt weekly."

## Candidate Solution (Python)

```python
import asyncio
from dataclasses import dataclass, field
from typing import List, Optional
from enum import Enum

class Severity(Enum):
    CRITICAL = "critical"
    WARNING = "warning"
    STYLE = "style"

@dataclass
class FileDiff:
    path: str
    additions: List[str]
    deletions: List[str]

@dataclass
class ReviewComment:
    file: str
    line: int
    severity: Severity
    message: str
    suggestion: Optional[str] = None

@dataclass
class ReviewReport:
    comments: List[ReviewComment] = field(default_factory=list)
    summary: str = ""
    score: int = 0

class StaticAnalysisAgent:
    async def review(self, diff: FileDiff) -> List[ReviewComment]:
        comments = []
        for i, line in enumerate(diff.additions):
            if "TODO" in line:
                comments.append(ReviewComment(
                    file=diff.path, line=i, severity=Severity.WARNING,
                    message="TODO found in production code",
                    suggestion="Resolve or track in issue tracker"
                ))
            if "print(" in line:
                comments.append(ReviewComment(
                    file=diff.path, line=i, severity=Severity.WARNING,
                    message="Debug print statement detected",
                    suggestion="Use logger instead"
                ))
        return comments

class LogicAgent:
    def __init__(self, llm_client):
        self.llm = llm_client

    async def review(self, diff: FileDiff) -> List[ReviewComment]:
        prompt = f"""Review this diff for semantic bugs:
File: {diff.path}
Changes:
{chr(10).join(diff.additions)}

Identify potential logic errors, race conditions, or off-by-one errors.
Return JSON: [{{"line": int, "message": str, "suggestion": str}}]"""
        response = await self.llm.generate_async(prompt)
        return [ReviewComment(file=diff.path, line=c["line"],
                severity=Severity.CRITICAL, message=c["message"],
                suggestion=c.get("suggestion")) for c in response]

class SupervisorAgent:
    def __init__(self, static_agent, logic_agent):
        self.static_agent = static_agent
        self.logic_agent = logic_agent

    async def review_pr(self, files: List[FileDiff]) -> ReviewReport:
        tasks = []
        for f in files:
            tasks.append(self.static_agent.review(f))
            tasks.append(self.logic_agent.review(f))
        results = await asyncio.gather(*tasks)
        comments = [c for r in results for c in r]
        comments.sort(key=lambda c: c.severity.value)
        summary = self._summarize(comments)
        return ReviewReport(comments=comments, summary=summary, score=len(comments))

    def _summarize(self, comments: List[ReviewComment]) -> str:
        critical = sum(1 for c in comments if c.severity == Severity.CRITICAL)
        warnings = sum(1 for c in comments if c.severity == Severity.WARNING)
        style = sum(1 for c in comments if c.severity == Severity.STYLE)
        return f"Found {critical} critical, {warnings} warnings, {style} style issues"

class CodeReviewAgent:
    def __init__(self, supervisor: SupervisorAgent, feedback_db=None):
        self.supervisor = supervisor
        self.feedback_db = feedback_db

    async def review_pull_request(self, pr_data: dict) -> dict:
        files = [FileDiff(path=f["path"], additions=f["additions"], deletions=f["deletions"])
                 for f in pr_data["files"]]
        report = await self.supervisor.review_pr(files)
        if self.feedback_db:
            report.comments = self._apply_feedback(report.comments)
        return {"report": report, "status": "changes_requested" if report.score > 0 else "approved"}

    def _apply_feedback(self, comments: List[ReviewComment]) -> List[ReviewComment]:
        if not self.feedback_db:
            return comments
        return [c for c in comments
                if not self.feedback_db.is_rejected_pattern(c.message)]
```

## Interviewer Feedback
"Great multi-agent design with clear separation of concerns. The async parallel review, feedback loop, and hallucination grounding are well thought out. I'd want to see more on integration testing."

## Key Takeaways
- Multi-agent architectures scale code review by specializing agents
- Map-reduce pattern handles large diffs efficiently
- Grounding comments in specific lines prevents hallucinations
- Feedback loops improve review quality over time
- Asynchronous execution improves latency for large PRs
