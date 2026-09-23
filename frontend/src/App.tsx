import { BrowserRouter, Routes, Route } from 'react-router-dom'
import './App.css'
import Layout from './components/Layout'
import Dashboard from './pages/Dashboard'
import Patients from './pages/Patients'
import Reports from './pages/Reports'
import TestMaster from './pages/TestMaster'
import ReportDetails from './pages/ReportDetails'
import CreateReport from './pages/CreateReport'

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/patients" element={<Patients />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/reports/create" element={<CreateReport />} />
          <Route path="/reports/:id" element={<ReportDetails />} />
          <Route path="/test-master" element={<TestMaster />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}
