const API_BASE_URL = '/api'
async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: { 'Content-Type': 'application/json', ...(options?.headers || {}) },
  })
  if (!response.ok) {
    const data = await response.json().catch(() => null)
    throw new Error(data?.error || data?.message || `Request failed (${response.status})`)
  }
  if (response.status === 204) return undefined as T
  return response.json()
}

export interface Patient { id:number; patientName:string; sex:string; dateOfBirth:string; phone:string }
export interface ReportResult { id:number; testId:number; testName:string; sectionName:string; unit:string; normalRange:string; resultValue:string; remarks:string|null; displayOrder:number }
export interface Report { id:number; patientId:number; patientName:string; sex:string; dateOfBirth:string; phone:string; referenceNo:string; referredBy:string; reportDate:string; status:'DRAFT'|'FINAL'; results:ReportResult[] }
export interface TestSection { id:number; name:string; displayOrder:number; active:boolean }
export interface TestDefinition { id:number; testName:string; sectionName:string; sectionId?:number; unit:string; normalRange:string; displayOrder:number; resultType?:string; active:boolean }
export interface CreatePatientRequest { patientName:string; sex?:string; dateOfBirth?:string; phone?:string }
export interface CreateReportRequest { patientId:number; referenceNo:string; referredBy?:string; reportDate?:string; status?:'DRAFT'|'FINAL' }
export interface ResultRequest { testId:number; resultValue:string; remarks?:string }
export interface SaveResultsRequest { results:ResultRequest[] }
export interface TestSectionRequest { name:string; displayOrder?:number; active?:boolean }
export interface TestDefinitionRequest { testName:string; sectionId:number; unit?:string; normalRange?:string; displayOrder?:number; resultType?:string; active?:boolean }

export const getPatients = () => request<Patient[]>('/patients')
export const getPatient = (id:number) => request<Patient>(`/patients/${id}`)
export const createPatient = (body:CreatePatientRequest) => request<Patient>('/patients',{method:'POST',body:JSON.stringify(body)})
export const updatePatient = (id:number, body:CreatePatientRequest) => request<Patient>(`/patients/${id}`,{method:'PUT',body:JSON.stringify(body)})
export const deletePatient = (id:number) => request<void>(`/patients/${id}`,{method:'DELETE'})
export const getReports = () => request<Report[]>('/reports')
export const createReport = (body:CreateReportRequest) => request<Report>('/reports',{method:'POST',body:JSON.stringify(body)})
export const saveResults = (id:number, body:SaveResultsRequest) => request<Report>(`/reports/${id}/results`,{method:'POST',body:JSON.stringify(body)})
export const updateReportStatus = (id:number,status:'DRAFT'|'FINAL') => request<Report>(`/reports/${id}/status`,{method:'PATCH',body:JSON.stringify({status})})
export const deleteReport = (id:number) => request<void>(`/reports/${id}`,{method:'DELETE'})
export const getTests = () => request<TestDefinition[]>('/tests')
export const getTestSections = () => request<TestSection[]>('/test-sections')
export const createTestSection = (body:TestSectionRequest) => request<TestSection>('/test-sections',{method:'POST',body:JSON.stringify(body)})
export const createTest = (body:TestDefinitionRequest) => request<TestDefinition>('/tests',{method:'POST',body:JSON.stringify(body)})
export const updateTest = (id:number,body:TestDefinitionRequest) => request<TestDefinition>(`/tests/${id}`,{method:'PUT',body:JSON.stringify(body)})
export const deleteTest = (id:number) => request<void>(`/tests/${id}`,{method:'DELETE'})
export const pdfUrl = (id:number) => `${API_BASE_URL}/reports/${id}/pdf`
