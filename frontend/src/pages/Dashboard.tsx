import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getPatients, getReports, getTests, type Patient, type Report, type TestDefinition } from '../api'

export default function Dashboard() {
  const [patients,setPatients]=useState<Patient[]>([]); const [reports,setReports]=useState<Report[]>([]); const [tests,setTests]=useState<TestDefinition[]>([])
  const [loading,setLoading]=useState(true); const [error,setError]=useState('')
  useEffect(()=>{Promise.all([getPatients(),getReports(),getTests()]).then(([p,r,t])=>{setPatients(p);setReports(r);setTests(t)}).catch(e=>setError(e.message)).finally(()=>setLoading(false))},[])
  const drafts=reports.filter(r=>r.status==='DRAFT').length, finals=reports.filter(r=>r.status==='FINAL').length
  const recent=[...reports].sort((a,b)=>b.id-a.id).slice(0,6)
  return <div>
    <div className="page-heading"><div><p className="eyebrow">OVERVIEW</p><h2>Good to see you, Administrator</h2><p className="muted">Here’s what is happening in your laboratory today.</p></div><Link className="button primary" to="/reports/create">＋ New Report</Link></div>
    {error&&<div className="alert error">{error}</div>}
    <div className="stat-grid">
      <Stat label="Total Patients" value={loading?'—':patients.length} tone="blue" meta="Registered patients" />
      <Stat label="Total Reports" value={loading?'—':reports.length} tone="purple" meta="All pathology reports" />
      <Stat label="Draft Reports" value={loading?'—':drafts} tone="amber" meta="Awaiting completion" />
      <Stat label="Final Reports" value={loading?'—':finals} tone="green" meta="Ready for delivery" />
    </div>
    <div className="dashboard-grid">
      <section className="card recent-card"><div className="card-head"><div><h3>Recent reports</h3><p className="muted">Latest reports created in the system</p></div><Link to="/reports" className="text-link">View all →</Link></div>
        {loading?<div className="empty">Loading reports…</div>:recent.length===0?<div className="empty">No reports yet.</div>:<div className="compact-table"><div className="tr th"><span>Reference</span><span>Patient</span><span>Date</span><span>Status</span></div>{recent.map(r=><Link to={`/reports/${r.id}`} className="tr" key={r.id}><span className="strong">{r.referenceNo}</span><span>{r.patientName}</span><span>{r.reportDate||'—'}</span><span><Status status={r.status}/></span></Link>)}</div>}
      </section>
      <section className="card"><div className="card-head"><div><h3>Quick actions</h3><p className="muted">Common laboratory tasks</p></div></div><div className="quick-grid"><Link to="/patients"><b>＋</b><span>Add patient</span><small>Register a new patient</small></Link><Link to="/reports/create"><b>▤</b><span>Create report</span><small>Start a pathology report</small></Link><Link to="/reports"><b>↗</b><span>View reports</span><small>Search and manage reports</small></Link><Link to="/test-master"><b>⚗</b><span>Test master</span><small>Manage tests and sections</small></Link></div></section>
    </div>
    <div className="info-strip"><div><strong>{tests.length}</strong><span>active test definitions</span></div><div><strong>{patients.length}</strong><span>patients on record</span></div><div><strong>{finals}</strong><span>final reports</span></div></div>
  </div>
}
function Stat({label,value,meta,tone}:{label:string,value:string|number,meta:string,tone:string}){return <div className="stat-card"><span className={`stat-icon ${tone}`}>◈</span><div><span className="stat-label">{label}</span><strong>{value}</strong><small>{meta}</small></div></div>}
export function Status({status}:{status:'DRAFT'|'FINAL'}){return <span className={`badge ${status.toLowerCase()}`}><i/> {status}</span>}
