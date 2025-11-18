import React, {useState} from 'react';
import api from '../api';

export default function AdminSqlPanel() {
  const [sql,setSql] = useState("SELECT * FROM users LIMIT 3;");
  const [result,setResult] = useState(null);

  const run = async () => {
    try {
      const res = await api.post('/admin/sql', {sql});
      setResult(res.data);
    } catch (e) {
      setResult({error: e.response?.data || e.message});
    }
  };

  return (
    <div>
      <h3>Admin SQL</h3>
      <textarea value={sql} onChange={e=>setSql(e.target.value)} rows={6} style={{width:"100%"}} />
      <br/>
      <button onClick={run}>Run</button>
      <pre>{JSON.stringify(result,null,2)}</pre>
    </div>
  );
}
