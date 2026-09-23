import type { ReactNode } from "react";
import { type SubmitEvent, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import {
  createPatient,
  deletePatient,
  getPatients,
  updatePatient,
  type Patient,
} from "../api";

const blank = { patientName: "", sex: "", dateOfBirth: "", phone: "" };
export default function Patients() {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [query, setQuery] = useState("");
  const [show, setShow] = useState(false);
  const [editing, setEditing] = useState<Patient | null>(null);
  const [form, setForm] = useState(blank);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const load = () =>
    getPatients()
      .then(setPatients)
      .catch((e) => setError(e.message));
  useEffect(() => {
    load();
  }, []);
  const filtered = useMemo(
    () =>
      patients.filter((p) =>
        `${p.patientName} ${p.phone || ""} ${p.id}`
          .toLowerCase()
          .includes(query.toLowerCase()),
      ),
    [patients, query],
  );
  function open(p?: Patient) {
    setError("");
    setEditing(p || null);
    setForm(
      p
        ? {
            patientName: p.patientName,
            sex: p.sex || "",
            dateOfBirth: p.dateOfBirth || "",
            phone: p.phone || "",
          }
        : blank,
    );
    setShow(true);
  }
  async function submit(e: SubmitEvent) {
    e.preventDefault();
    if (!form.patientName.trim()) {
      setError("Patient name is required");
      return;
    }
    try {
      setSaving(true);
      if (editing) await updatePatient(editing.id, form);
      else await createPatient(form);
      setShow(false);
      await load();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Unable to save patient");
    } finally {
      setSaving(false);
    }
  }
  async function remove(p: Patient) {
    if (!confirm(`Delete ${p.patientName}?`)) return;
    try {
      await deletePatient(p.id);
      await load();
    } catch (e) {
      setError(e instanceof Error ? e.message : "Unable to delete patient");
    }
  }
  return (
    <div>
      <div className="page-heading">
        <div>
          <p className="eyebrow">PATIENTS</p>
          <h2>Patient directory</h2>
          <p className="muted">Register and maintain patient information.</p>
        </div>
        <button className="button primary" onClick={() => open()}>
          ＋ Add Patient
        </button>
      </div>
      {error && <div className="alert error">{error}</div>}
      <section className="card">
        <div className="toolbar">
          <div className="search">
            <span>⌕</span>
            <input
              placeholder="Search by name, phone or ID…"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>
          <span className="count">{filtered.length} patients</span>
        </div>
        <div className="data-table">
          <div className="tr th">
            <span>ID</span>
            <span>Patient</span>
            <span>Sex</span>
            <span>Date of birth</span>
            <span>Phone</span>
            <span>Actions</span>
          </div>
          {filtered.map((p) => (
            <div className="tr" key={p.id}>
              <span className="muted">#{p.id}</span>
              <span>
                <strong>{p.patientName}</strong>
              </span>
              <span>{p.sex || "—"}</span>
              <span>{p.dateOfBirth || "—"}</span>
              <span>{p.phone || "—"}</span>
              <span className="row-actions">
                <button className="icon-button" onClick={() => open(p)}>
                  Edit
                </button>
                <button
                  className="icon-button danger"
                  onClick={() => remove(p)}
                >
                  Delete
                </button>
                <Link className="icon-button" to={`/reports?patient=${p.id}`}>
                  Reports
                </Link>
              </span>
            </div>
          ))}
          {!filtered.length && (
            <div className="empty">No patients match your search.</div>
          )}
        </div>
      </section>
      {show && (
        <div className="modal-backdrop">
          <div className="modal">
            <div className="modal-head">
              <div>
                <p className="eyebrow">PATIENT</p>
                <h3>{editing ? "Edit patient" : "Add patient"}</h3>
              </div>
              <button className="close" onClick={() => setShow(false)}>
                ×
              </button>
            </div>
            <form onSubmit={submit}>
              <div className="form-grid">
                <Field label="Patient name *">
                  <input
                    value={form.patientName}
                    onChange={(e) =>
                      setForm({ ...form, patientName: e.target.value })
                    }
                    autoFocus
                    placeholder="e.g. KRUNAL AGRAWAL"
                  />
                </Field>
                <Field label="Sex">
                  <select
                    value={form.sex}
                    onChange={(e) => setForm({ ...form, sex: e.target.value })}
                  >
                    <option value="">Select sex</option>
                    <option>Male</option>
                    <option>Female</option>
                  </select>
                </Field>
                <Field label="Date of birth">
                  <input
                    type="date"
                    value={form.dateOfBirth}
                    onChange={(e) =>
                      setForm({ ...form, dateOfBirth: e.target.value })
                    }
                  />
                </Field>
                <Field label="Phone">
                  <input
                    value={form.phone}
                    onChange={(e) =>
                      setForm({ ...form, phone: e.target.value })
                    }
                    placeholder="10-digit phone number"
                  />
                </Field>
              </div>
              <div className="modal-actions">
                <button
                  type="button"
                  className="button secondary"
                  onClick={() => setShow(false)}
                >
                  Cancel
                </button>
                <button className="button primary" disabled={saving}>
                  {saving
                    ? "Saving…"
                    : editing
                      ? "Update patient"
                      : "Save patient"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
function Field({ label, children }: { label: string; children: ReactNode }) {
  return (
    <label className="field">
      <span>{label}</span>
      {children}
    </label>
  );
}
