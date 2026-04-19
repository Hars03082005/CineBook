import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

const timeConfig = {
  '10:00': { label:'Morning 10:00 AM', color:'#ffdd00', bg:'#ffdd0015', icon:'🌅' },
  '14:00': { label:'Afternoon 2:00 PM', color:'#ff8800', bg:'#ff880015', icon:'☀️' },
  '18:00': { label:'Evening 6:00 PM',   color:'#aa44ff', bg:'#aa44ff15', icon:'🌆' },
  '21:30': { label:'Night 9:30 PM',     color:'#4488ff', bg:'#4488ff15', icon:'🌙' },
};

export default function ShowSelection() {
  const { movieId } = useParams();
  const navigate = useNavigate();

  const [movie, setMovie]     = useState(null);
  const [shows, setShows]     = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setDate] = useState(0);  // 0=today, 1=tomorrow, 2=day after

  const dates = [0,1,2,3].map(offset => {
    const d = new Date(); d.setDate(d.getDate() + offset);
    return {
      offset,
      label: offset === 0 ? 'Today' : offset === 1 ? 'Tomorrow' : d.toLocaleDateString('en-IN',{weekday:'short'}),
      full:  d.toISOString().split('T')[0],
      nice:  d.toLocaleDateString('en-IN',{ day:'numeric', month:'short' }),
    };
  });

  useEffect(() => { fetchData(); }, [movieId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [movieRes, showRes] = await Promise.all([
        api.get(`/api/movies/${movieId}`),
        api.get(`/api/shows/movie/${movieId}`),
      ]);
      setMovie(movieRes.data.data);
      setShows(showRes.data.data || []);
    } catch (err) { console.error(err); }
    finally { setLoading(false); }
  };

  // Filter shows for selected date
  const selectedDateStr = dates[selectedDate].full;
  const showsForDate = shows.map(theatreGroup => ({
    ...theatreGroup,
    showsByDate: {
      [selectedDateStr]: (theatreGroup.showsByDate?.[selectedDateStr] || []).filter(show => {
        const dt = new Date(`${show.showDate}T${show.showTime}`);
        return dt.getTime() > Date.now();
      }),
    },
  })).filter(g => (g.showsByDate[selectedDateStr] || []).length > 0);

  const getTimeConfig = (timeStr) => {
    const hour = timeStr?.split(':')[0] + ':' + timeStr?.split(':')[1];
    return timeConfig[hour.substring(0,5)] || { label: timeStr, color:'#888', bg:'#88888815', icon:'🎬' };
  };

  return (
    <div style={{ minHeight:'100vh', background:'#07070e', fontFamily:"'Segoe UI',sans-serif" }}>
      <Navbar theme="user"/>

      {/* Movie header */}
      {movie && (
        <div style={{
          background:'linear-gradient(135deg,#0d0d1a,#1a0a2e)',
          padding:'28px 24px', borderBottom:'1px solid #ffffff11',
        }}>
          <div style={{ maxWidth:'1100px', margin:'0 auto' }}>
            <button onClick={() => navigate('/movies')} style={{
              background:'none', border:'none', color:'#667799', cursor:'pointer',
              fontSize:'14px', marginBottom:'12px',
            }}>← Back to Movies</button>
            <div style={{ display:'flex', alignItems:'center', gap:'20px' }}>
              <div style={{
                width:'60px', height:'60px', borderRadius:'12px',
                background:'linear-gradient(135deg,#1a1a2e,#0f3460)',
                display:'flex', alignItems:'center', justifyContent:'center',
                fontSize:'28px', fontWeight:'900', color:'rgba(255,255,255,0.3)',
              }}>{movie.title?.[0]}</div>
              <div>
                <h1 style={{ color:'#fff', margin:'0 0 4px', fontSize:'22px', fontWeight:'900' }}>
                  {movie.title}
                </h1>
                <div style={{ color:'#667799', fontSize:'13px', display:'flex', gap:'16px' }}>
                  <span>🎭 {movie.genre}</span>
                  <span>🌐 {movie.language}</span>
                  <span>⏱ {movie.durationMinutes} min</span>
                  <span>⭐ {movie.rating}</span>
                  <span style={{
                    background:'rgba(255,255,255,0.1)', padding:'1px 8px',
                    borderRadius:'4px', color:'#ccc',
                  }}>{movie.certificate}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      <div style={{ maxWidth:'1100px', margin:'0 auto', padding:'24px' }}>

        {/* Date tabs */}
        <div style={{ display:'flex', gap:'10px', marginBottom:'28px', overflowX:'auto' }}>
          {dates.map((d, i) => (
            <button key={i} onClick={() => setDate(i)} style={{
              padding:'10px 24px', borderRadius:'12px', border:'none',
              cursor:'pointer', minWidth:'100px', transition:'all 0.2s',
              background: selectedDate === i
                ? 'linear-gradient(135deg,#00d4ff,#0066ff)'
                : 'rgba(255,255,255,0.05)',
              color: selectedDate === i ? '#fff' : '#888',
              fontWeight: selectedDate === i ? '800' : '500',
              boxShadow: selectedDate === i ? '0 4px 15px #00d4ff33' : 'none',
            }}>
              <div style={{ fontSize:'13px' }}>{d.label}</div>
              <div style={{ fontSize:'11px', opacity:0.8 }}>{d.nice}</div>
            </button>
          ))}
        </div>

        {loading ? (
          <div style={{ textAlign:'center', padding:'80px', color:'#667799' }}>
            <div style={spinner}/><p style={{ marginTop:'16px' }}>Loading shows...</p>
          </div>
        ) : showsForDate.length === 0 ? (
          <div style={{ textAlign:'center', padding:'60px', color:'#667799' }}>
            <div style={{ fontSize:'48px' }}>🚫</div>
            <p>No shows available for this date.</p>
          </div>
        ) : (
          <div style={{ display:'flex', flexDirection:'column', gap:'16px' }}>
            {showsForDate.map((group, gi) => {
              const theatre  = group.theatre;
              const dayShows = group.showsByDate[selectedDateStr] || [];
              return (
                <div key={gi} style={{
                  background:'rgba(255,255,255,0.03)',
                  border:'1px solid #ffffff0d', borderRadius:'16px', padding:'20px 24px',
                }}>
                  <div style={{ marginBottom:'16px' }}>
                    <h3 style={{ color:'#fff', margin:'0 0 4px', fontSize:'16px', fontWeight:'800' }}>
                      🎪 {theatre.name}
                    </h3>
                    <p style={{ color:'#667799', margin:0, fontSize:'12px' }}>
                      📍 {theatre.address} · {theatre.totalSeats} seats
                    </p>
                  </div>

                  <div style={{ display:'flex', gap:'12px', flexWrap:'wrap' }}>
                    {dayShows.sort((a,b)=>a.showTime.localeCompare(b.showTime)).map((show, si) => {
                      const tc = getTimeConfig(show.showTime);
                      const soldOut = show.availableSeats === 0;
                      return (
                        <button key={si}
                          disabled={soldOut}
                          onClick={() => navigate(`/shows/${show.id}/seats`)}
                          style={{
                            padding:'12px 20px', borderRadius:'12px', border:`2px solid ${soldOut?'#333':tc.color+'44'}`,
                            background: soldOut ? '#1a1a1a' : tc.bg,
                            color: soldOut ? '#444' : tc.color,
                            cursor: soldOut ? 'not-allowed' : 'pointer',
                            minWidth:'140px', textAlign:'center', transition:'all 0.2s',
                            opacity: soldOut ? 0.5 : 1,
                          }}
                          onMouseOver={e => { if(!soldOut) e.currentTarget.style.background=tc.color+'30'; }}
                          onMouseOut={e  => { if(!soldOut) e.currentTarget.style.background=tc.bg; }}>
                          <div style={{ fontSize:'18px', marginBottom:'2px' }}>{tc.icon}</div>
                          <div style={{ fontWeight:'800', fontSize:'14px' }}>
                            {show.showTime.slice(0,5).length === 5
                              ? formatTime(show.showTime)
                              : show.showTime}
                          </div>
                          <div style={{ fontSize:'11px', opacity:0.8, marginTop:'2px' }}>
                            {soldOut ? '🚫 Sold Out' : `₹${show.price} · ${show.availableSeats} left`}
                          </div>
                        </button>
                      );
                    })}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
      <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
    </div>
  );
}

function formatTime(t) {
  const [h, m] = t.split(':');
  const hr = parseInt(h);
  const ampm = hr >= 12 ? 'PM' : 'AM';
  return `${hr%12||12}:${m} ${ampm}`;
}

const spinner = {
  width:'36px', height:'36px', margin:'0 auto',
  border:'3px solid #ffffff11', borderTop:'3px solid #00d4ff',
  borderRadius:'50%', animation:'spin 0.8s linear infinite',
};
