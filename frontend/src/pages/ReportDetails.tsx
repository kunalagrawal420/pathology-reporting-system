import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  getReports,
  getTests,
  pdfUrl,
  saveResults,
  updateReportStatus,
  type Report,
  type TestDefinition,
} from "../api";
import { Status } from "./Dashboard";
interface ResultForm {
  testId: number;
  resultValue: string;
  remarks: string;
}
export default function ReportDetails() {
  const { id } = useParams();
  const [report, setReport] = useState<Report | null>(null);
  const [tests, setTests] = useState<TestDefinition[]>([]);
  const [results, setResults] = useState<ResultForm[]>([]);
  const [editing, setEditing] = useState(false);
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  async function load() {
    try {
      setLoading(true);
      setError("");
      const [rs, ts] = await Promise.all([getReports(), getTests()]);
      const r = rs.find((x) => x.id === Number(id));
      if (!r) {
        setError("Report not found");
        return;
      }
      setReport(r);
      setTests(ts);
      setResults(
        r.results.map((x) => ({
          testId: x.testId,
          resultValue: x.resultValue || "",
          remarks: x.remarks || "",
        })),
      );
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to load report");
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => {
    load();
  }, [id]);
  const sections = useMemo(
    () =>
      Array.from(
        new Set(
          tests
            .filter((t) => t.active !== false)
            .sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0))
            .map((t) => t.sectionName),
        ),
      ),
    [tests],
  );
  const getResult = (testId: number) =>
    results.find((x) => x.testId === testId) || {
      testId,
      resultValue: "",
      remarks: "",
    };
  const setResult = (
    testId: number,
    field: "resultValue" | "remarks",
    value: string,
  ) =>
    setResults((cur) =>
      cur.some((x) => x.testId === testId)
        ? cur.map((x) => (x.testId === testId ? { ...x, [field]: value } : x))
        : [
            ...cur,
            {
              testId,
              resultValue: field === "resultValue" ? value : "",
              remarks: field === "remarks" ? value : "",
            },
          ],
    );
  async function save() {
    if (!report) return;
    const body = results
      .filter((x) => x.resultValue.trim())
      .map((x) => ({
        testId: x.testId,
        resultValue: x.resultValue.trim(),
        remarks: x.remarks.trim() || undefined,
      }));
    if (!body.length) {
      setError("Enter at least one result before saving.");
      return;
    }
    try {
      setBusy(true);
      setError("");
      const r = await saveResults(report.id, { results: body });
      setReport(r);
      setResults(
        r.results.map((x) => ({
          testId: x.testId,
          resultValue: x.resultValue || "",
          remarks: x.remarks || "",
        })),
      );
      setEditing(false);
      setSuccess("Results saved successfully.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to save results");
    } finally {
      setBusy(false);
    }
  }
  async function finalize() {
    if (
      !report ||
      !confirm("Finalize this report? Final reports should not be edited.")
    )
      return;
    try {
      setBusy(true);
      const r = await updateReportStatus(report.id, "FINAL");
      setReport(r);
      setEditing(false);
      setSuccess("Report finalized successfully.");
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to finalize report");
    } finally {
      setBusy(false);
    }
  }
  if (loading) return <div className="loading">Loading report…</div>;
  if (!report)
    return (
      <div className="empty-page">
        <div className="empty-icon">!</div>
        <h2>Report not found</h2>
        <p>{error}</p>
        <Link className="button secondary" to="/reports">
          Back to reports
        </Link>
      </div>
    );
  return (
    <div>
      <div className="page-heading">
        <div>
          <div className="heading-inline">
            <p className="eyebrow">REPORT #{report.id}</p>
            <Status status={report.status} />
          </div>
          <h2>{report.patientName}</h2>
          <p className="muted">
            Reference <strong>{report.referenceNo}</strong> ·{" "}
            {report.reportDate || "No date"}
          </p>
        </div>
        <div className="heading-actions">
          <Link className="button secondary" to="/reports">
            ← Reports
          </Link>
          {report.status === "DRAFT" && !editing && (
            <>
              <button
                className="button secondary"
                onClick={() => {
                  setEditing(true);
                  setSuccess("");
                }}
              >
                Edit results
              </button>
              <button
                className="button primary"
                onClick={finalize}
                disabled={busy}
              >
                {busy ? "Finalizing…" : "Finalize report"}
              </button>
            </>
          )}
          {report.status === "FINAL" && !editing && (
            <button
              className="button primary"
              onClick={() => window.open(pdfUrl(report.id), "_blank")}
            >
              View / Print PDF ↗
            </button>
          )}
        </div>
      </div>
      {error && <div className="alert error">{error}</div>}
      {success && <div className="alert success">✓ {success}</div>}
      <section className="card patient-summary">
        <div className="summary-main">
          <div className="patient-avatar">{report.patientName.charAt(0)}</div>
          <div>
            <span className="muted">Patient</span>
            <h3>{report.patientName}</h3>
            <p>
              {report.sex || "—"} · DOB {report.dateOfBirth || "—"} ·{" "}
              {report.phone || "No phone"}
            </p>
          </div>
        </div>
        <div className="summary-meta">
          <div>
            <span>Referred by</span>
            <strong>{report.referredBy || "—"}</strong>
          </div>
          <div>
            <span>Reference</span>
            <strong>{report.referenceNo}</strong>
          </div>
        </div>
      </section>
      <div className="results-heading">
        <div>
          <p className="eyebrow">RESULTS</p>
          <h3>Investigation results</h3>
        </div>
        {editing && <span className="edit-note">Editing draft report</span>}
      </div>
      {sections.map((section) => {
        const rows = tests
          .filter((t) => t.active !== false && t.sectionName === section)
          .sort((a, b) => (a.displayOrder || 0) - (b.displayOrder || 0));
        return (
          <section className="result-card card" key={section}>
            <div className="section-title">
              <span className="section-dot" />
              <h3>{section}</h3>
              <span>{rows.length} tests</span>
            </div>
            <div className="result-table">
              <div className="result-row result-head">
                <span>Test</span>
                <span>Result</span>
                <span>Unit</span>
                <span>Reference range</span>
                <span>Remarks</span>
              </div>
              {rows.map((t) => {
                const r = getResult(t.id);
                return (
                  <div className="result-row" key={t.id}>
                    <div>
                      <strong>{t.testName}</strong>
                    </div>
                    {editing ? (
                      <input
                        className="result-input"
                        value={r.resultValue}
                        onChange={(e) =>
                          setResult(t.id, "resultValue", e.target.value)
                        }
                        placeholder="Enter result"
                      />
                    ) : (
                      <strong className={r.resultValue ? "result-number" : ""}>
                        {r.resultValue || "—"}
                      </strong>
                    )}
                    <span>{t.unit || "—"}</span>
                    <span>{t.normalRange || "—"}</span>
                    {editing ? (
                      <input
                        className="result-input"
                        value={r.remarks}
                        onChange={(e) =>
                          setResult(t.id, "remarks", e.target.value)
                        }
                        placeholder="Optional"
                      />
                    ) : (
                      <span>{r.remarks || "—"}</span>
                    )}
                  </div>
                );
              })}
            </div>
          </section>
        );
      })}
      {editing && (
        <div className="sticky-actions">
          <button
            className="button secondary"
            onClick={() => {
              setEditing(false);
              load();
            }}
            disabled={busy}
          >
            Cancel
          </button>
          <button className="button primary" onClick={save} disabled={busy}>
            {busy ? "Saving…" : "Save results"}
          </button>
        </div>
      )}
    </div>
  );
}
