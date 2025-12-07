// Простой клиентский JS для веб‑интерфейса — использует fetch() для вызова REST API
document.addEventListener('DOMContentLoaded', () => {
  const tabs = {
    patients: document.getElementById('patients-section'),
    doctors: document.getElementById('doctors-section'),
    appointments: document.getElementById('appointments-section')
  };
  // Кэши для сопоставления id -> объект для удобного отображения
  let patientsById = {};
  let doctorsById = {};
  // Listen for notifications from popup windows (BroadcastChannel or localStorage fallback)
  try {
    if (typeof BroadcastChannel !== 'undefined') {
      const bc = new BroadcastChannel('medsys');
      bc.onmessage = (ev) => {
        if (ev.data === 'patients') refreshPatients();
        if (ev.data === 'doctors') refreshDoctors();
        if (ev.data === 'appointments') refreshAppointments();
      };
    }
  } catch (e) {
    // ignore
  }
  window.addEventListener('storage', (e) => {
    if (!e) return;
    if (e.key === 'medsys-refresh' && e.newValue) {
      if (e.newValue.indexOf('patients') === 0) refreshPatients();
      if (e.newValue.indexOf('doctors') === 0) refreshDoctors();
      if (e.newValue.indexOf('appointments') === 0) refreshAppointments();
    }
  });
  document.getElementById('tab-patients').addEventListener('click', () => showTab('patients'));
  document.getElementById('tab-doctors').addEventListener('click', () => showTab('doctors'));
  document.getElementById('tab-appointments').addEventListener('click', () => showTab('appointments'));
  // Open create forms in new windows
  const openCreatePatientBtn = document.getElementById('open-create-patient');
  if (openCreatePatientBtn) openCreatePatientBtn.addEventListener('click', () => window.open('/create/patient', '_blank', 'width=600,height=600'));
  const openCreateDoctorBtn = document.getElementById('open-create-doctor');
  if (openCreateDoctorBtn) openCreateDoctorBtn.addEventListener('click', () => window.open('/create/doctor', '_blank', 'width=600,height=700'));
  const openCreateAppointmentBtn = document.getElementById('open-create-appointment');
  if (openCreateAppointmentBtn) openCreateAppointmentBtn.addEventListener('click', () => window.open('/create/appointment', '_blank', 'width=700,height=700'));
  function showTab(name) {
    Object.keys(tabs).forEach(k => tabs[k].style.display = (k === name) ? '' : 'none');
    if (name === 'appointments') refreshAppointments();
    if (name === 'patients') refreshPatients();
    if (name === 'doctors') refreshDoctors();
  }
  // Inline creation moved to separate windows; patient creation handled in `/create/patient` window.
  async function refreshPatients(){
    const res = await fetch('/api/patients');
    const list = await res.json();
    // Обновить кэш
    patientsById = {};
    list.forEach(p => patientsById[p.id] = p);
    const table = document.getElementById('patients-table');
    table.innerHTML = '<tr><th>ID</th><th>Имя</th><th>Дата рождения</th><th>Телефон</th><th>ИИН</th><th>Действия</th></tr>' +
      list.map(p => `<tr><td>${p.id}</td><td>${p.firstName} ${p.lastName}</td><td>${p.birthDate}</td><td>${p.phone}</td><td>${p.iin}</td><td><button data-id="${p.id}" class="edit-patient">Редактировать</button> <button data-id="${p.id}" class="del-patient">Удалить</button></td></tr>`).join('');
    document.querySelectorAll('.edit-patient').forEach(b => b.addEventListener('click', (ev) => {
      const id = ev.target.dataset.id;
      window.open('/edit/patient?id=' + id, '_blank', 'width=600,height=600');
    }));
    document.querySelectorAll('.del-patient').forEach(b => b.addEventListener('click', async (ev) => {
      const id = ev.target.dataset.id;
      await fetch('/api/patients?id=' + id, { method: 'DELETE' });
      refreshPatients();
    }));
    // no inline patient select on main page
  }
  // Inline doctor creation moved to `/create/doctor` window.
  async function refreshDoctors(){
    const res = await fetch('/api/doctors');
    const list = await res.json();
    // Обновить кэш
    doctorsById = {};
    list.forEach(d => doctorsById[d.id] = d);
    const table = document.getElementById('doctors-table');
    table.innerHTML = '<tr><th>ID</th><th>Имя</th><th>Специальность</th><th>Телефон</th><th>Email</th><th>ИИН</th><th>Действия</th></tr>' +
      list.map(d => `<tr><td>${d.id}</td><td>${d.firstName} ${d.lastName}</td><td>${d.specialization}</td><td>${d.phone}</td><td>${d.email}</td><td>${d.iin}</td><td><button data-id="${d.id}" class="edit-doctor">Редактировать</button> <button data-id="${d.id}" class="del-doctor">Удалить</button></td></tr>`).join('');
    document.querySelectorAll('.edit-doctor').forEach(b => b.addEventListener('click', (ev) => {
      const id = ev.target.dataset.id;
      window.open('/edit/doctor?id=' + id, '_blank', 'width=650,height=700');
    }));
    document.querySelectorAll('.del-doctor').forEach(b => b.addEventListener('click', async (ev) => {
      const id = ev.target.dataset.id;
      await fetch('/api/doctors?id=' + id, { method: 'DELETE' });
      refreshDoctors();
    }));
    // no inline doctor select on main page
  }
  // Inline appointment creation moved to `/create/appointment` window.
  async function refreshAppointments(){
    await refreshPatients();
    await refreshDoctors();
    const res = await fetch('/api/appointments');
    const list = await res.json();
    const table = document.getElementById('appointments-table');
    table.innerHTML = '<tr><th>ID</th><th>Пациент</th><th>Врач</th><th>Специальность</th><th>Дата и время</th><th>Тип приёма</th><th>Статус</th><th>Диагноз</th><th>Действия</th></tr>' +
      list.map(a => {
        const p = patientsById[a.patientId];
        const d = doctorsById[a.doctorId];
        const patientName = p ? `${p.firstName} ${p.lastName}` : a.patientId;
        const doctorName = d ? `${d.firstName} ${d.lastName}` : a.doctorId;
        const doctorSpec = d ? (d.specialization || '') : '';
        const appointmentType = a.typeName || 'Не указан';
        const status = a.isOverdue ? '<span style="color:red">Просрочен</span>' : '<span style="color:green">Активен</span>';
        return `<tr><td>${a.id}</td><td>${patientName}</td><td>${doctorName}</td><td>${doctorSpec}</td><td>${a.dateTime}</td><td>${appointmentType}</td><td>${status}</td><td>${a.diagnosis}</td><td><button data-id="${a.id}" class="edit-appt">Редактировать</button> <button data-id="${a.id}" class="del-appt">Удалить</button></td></tr>`;
      }).join('');
    document.querySelectorAll('.edit-appt').forEach(b => b.addEventListener('click', (ev) => {
      const id = ev.target.dataset.id;
      window.open('/edit/appointment?id=' + id, '_blank', 'width=700,height=700');
    }));
    document.querySelectorAll('.del-appt').forEach(b => b.addEventListener('click', async (ev) => {
      const id = ev.target.dataset.id;
      await fetch('/api/appointments?id=' + id, { method: 'DELETE' });
      refreshAppointments();
    }));
  }
  // No inline selects on main page — creation is done in separate windows.
  showTab('patients');
});
