import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import Navbar from '../components/Navbar';

const SEAT_COLORS = {
  VIP:     { avail:'#b8860b', selected:'#ffd700', booked:'#2a2a1a', text:'#ffd700' },
  PREMIUM: { avail:'#4a1a6b', selected:'#aa44ff', booked:'#1a1a2a', text:'#aa44ff' },
  REGULAR: { avail:'#0a3a4a', selected:'#00d4ff', booked:'#1a2a2a', text:'#00d4ff' },
};

export default function SeatSelection() {
  const { showId } = useParams();
  const navigate   = useNavigate();

  const [data, setData]           = useState(null);
  const [loading, setLoading]     = useState(true);
  const [selected, setSelected]   = useState([]);
  const [booking, setBooking]     = useState(false);
  const [error, setError]         = useState('');

  useEffect(() => { fetchSeats(); }, [showId]);

  const fetchSeats = async () => {
    setLoading(true);
    try {
      const res = await api.get(`/api/shows/${showId}/seats`);
      setData(res.data.data);
    } catch { setError('Failed to load seats.'); }
    finally { setLoading(false); }
  };

  const toggleSeat = (seat) => {
    if (seat.booked) return;
    setSelected(prev => {
      const already = prev.find(s => s.id === seat.id);
      if (already) return prev.filter(s => s.id !== seat.id);
      if (prev.length >= 8) { setError('Maximum 8 seats allowed!'); return prev; }
      setError('');
      return [...prev, seat];
    });
  };

  const totalPrice = selected.reduce((sum, s) => sum + s.price, 0);

  const handleProceed = async () => {
    if (selected.length === 0) { setError('Please select at least one seat.'); return; }
    setBooking(true); setError('');
    try {
      const res = await api.post('/api/bookings', {
        showId,
        seatNumbers: selected.map(s => s.seatNumber),
      });
      const bookingId = res.data.data?.id;
      navigate(`/payment/${bookingId}`);
    } catch (err) {
      setError(err.response?.data?.message || 'Booking failed. Some seats may have been taken.');
      fetchSeats(); setSelected([]);
    } finally { setBooking(false); }
  };

  if (loading) return (
    <div style={{ minHeight:'100vh', background:'#07070e', display:'flex', alignItems:'center', justifyContent:'center' }}>
      <div><div style={spinner}/><p style={{ color:'#667799', textAlign:'center', marginTop:'16px' }}>Loading seats...</p></div>
    </div>
  );

  const seats  = data?.seats || [];
  const show   = data?.show;
  const movie  = data?.movie;
  const theatre = data?.theatre;
  const showStarted = show
    ? new Date(`${show.showDate}T${show.showTime}`).getTime() <= Date.now()
    : false;

  // Group by row
  const rows = ['A','B','C','D','E','F','G','H','I','J'];
  const seatsByRow = {};
  rows.forEach(r => {
    seatsByRow[r] = seats.filter(s => s.row === r)
                         .sort((a,b) => {
                           const numA = parseInt(a.seatNumber.slice(1));
                           const numB = parseInt(b.seatNumber.slice(1));
                           return numA - numB;
                         });
  });

  const rowType = (r) => ['A','B'].includes(r) ? 'VIP' : ['C','D','E'].includes(r) ? 'PREMIUM' : 'REGULAR';

  return (
    <div style={{ minHeight:'100vh', background:'#07070e', fontFamily:"'Segoe UI',sans-serif" }}>
      <Navbar theme="user"/>

      {/* Show header */}
      <div style={{ background:'#0d0d1a', padding:'16px 24px', borderBottom:'1px solid #ffffff11' }}>
        <div style={{ maxWidth:'1200px', margin:'0 auto' }}>
          <button onClick={() => navigate(-1)} style={{ background:'none', border:'none', color:'#667799', cursor:'pointer', fontSize:'13px', marginBottom:'6px' }}>← Back</button>
          <h2 style={{ color:'#fff', margin:'0 0 4px', fontSize:'18px', fontWeight:'900' }}>
            🎬 {movie?.title}
          </h2>
          <p style={{ color:'#667799', margin:0, fontSize:'13px' }}>
            🎪 {theatre?.name} · 📅 {show?.showDate} · ⏰ {show?.showTime?.slice(0,5)} · 💰 From ₹{show?.price}
          </p>
        </div>
      </div>

      <div style={{ display:'flex', gap:'0', maxWidth:'1200px', margin:'0 auto' }}>

        {/* SEAT GRID */}
        <div style={{ flex:1, padding:'24px', overflowX:'auto' }}>

          {/* SCREEN */}
          <div style={{ textAlign:'center', marginBottom:'28px' }}>
            <div style={{
              display:'inline-block', padding:'8px 80px 18px',
              background:'linear-gradient(180deg, rgba(255,255,255,0.15) 0%, transparent 100%)',
              borderRadius:'0 0 50% 50% / 0 0 30px 30px',
              borderBottom:'2px solid rgba(255,255,255,0.2)',
              color:'rgba(255,255,255,0.5)', fontSize:'12px', letterSpacing:'6px',
              fontWeight:'700', width:'min(500px, 90%)',
            }}>S C R E E N</div>
          </div>

          {/* Legend */}
          <div style={{ display:'flex', justifyContent:'center', gap:'20px', marginBottom:'20px', flexWrap:'wrap' }}>
            {[
              { type:'VIP',     label:'VIP (Rows A-B)',         color:'#ffd700' },
              { type:'PREMIUM', label:'Premium (Rows C-E)',      color:'#aa44ff' },
              { type:'REGULAR', label:'Regular (Rows F-J)',      color:'#00d4ff' },
              { type:'BOOKED',  label:'Booked',                  color:'#333'    },
            ].map(l => (
              <div key={l.type} style={{ display:'flex', alignItems:'center', gap:'6px' }}>
                <div style={{ width:'20px', height:'20px', borderRadius:'4px', background:l.color }}/>
                <span style={{ color:'#667799', fontSize:'12px' }}>{l.label}</span>
              </div>
            ))}
          </div>

          {/* Seat rows */}
          {error && (
            <div style={{
              background:'#ff446620', border:'1px solid #ff4466',
              color:'#ff4466', padding:'10px 16px', borderRadius:'8px',
              marginBottom:'16px', textAlign:'center', fontSize:'13px',
            }}>{error}</div>
          )}

          {showStarted && (
            <div style={{
              background:'#ff990022', border:'1px solid #ff9900',
              color:'#ffcc66', padding:'10px 16px', borderRadius:'8px',
              marginBottom:'16px', textAlign:'center', fontSize:'13px',
            }}>This show has already started. Seat booking is disabled.</div>
          )}

          <div style={{ display:'flex', flexDirection:'column', gap:'6px' }}>
            {rows.map(row => {
              const type   = rowType(row);
              const colors = SEAT_COLORS[type];
              const rowSeats = seatsByRow[row] || [];

              return (
                <div key={row} style={{ display:'flex', alignItems:'center', gap:'6px' }}>
                  {/* Row label */}
                  <div style={{
                    width:'24px', textAlign:'center', color: colors.text,
                    fontSize:'13px', fontWeight:'800', flexShrink:0,
                  }}>{row}</div>

                  {/* Aisle gap in middle */}
                  <div style={{ display:'flex', gap:'4px', flexWrap:'nowrap' }}>
                    {rowSeats.slice(0,7).map(seat => renderSeat(seat, colors, selected, toggleSeat))}
                  </div>
                  <div style={{ width:'12px' }}/>
                  <div style={{ display:'flex', gap:'4px', flexWrap:'nowrap' }}>
                    {rowSeats.slice(7,15).map(seat => renderSeat(seat, colors, selected, toggleSeat))}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Price legend */}
          <div style={{ display:'flex', justifyContent:'center', gap:'24px', marginTop:'20px', flexWrap:'wrap' }}>
            {[
              { label:'VIP',     price: show ? show.price * 2 : '—'   },
              { label:'PREMIUM', price: show ? show.price * 1.5 : '—'  },
              { label:'REGULAR', price: show ? show.price    : '—'     },
            ].map(p => (
              <span key={p.label} style={{ color:'#667799', fontSize:'12px' }}>
                {p.label}: ₹{p.price}
              </span>
            ))}
          </div>
        </div>

        {/* RIGHT PANEL */}
        <div style={{
          width:'280px', flexShrink:0, background:'rgba(255,255,255,0.02)',
          borderLeft:'1px solid #ffffff0a', padding:'24px', minHeight:'500px',
        }}>
          <h3 style={{ color:'#fff', margin:'0 0 16px', fontSize:'15px', fontWeight:'800' }}>
            🎫 Selected Seats ({selected.length}/8)
          </h3>

          {selected.length === 0 ? (
            <p style={{ color:'#445566', fontSize:'13px' }}>Click seats to select them</p>
          ) : (
            <>
              <div style={{ display:'flex', flexDirection:'column', gap:'8px', marginBottom:'16px' }}>
                {selected.map(s => (
                  <div key={s.id} style={{
                    display:'flex', justifyContent:'space-between', alignItems:'center',
                    background:'rgba(255,255,255,0.04)', borderRadius:'8px', padding:'8px 12px',
                  }}>
                    <div>
                      <div style={{ color:'#fff', fontWeight:'700', fontSize:'14px' }}>{s.seatNumber}</div>
                      <div style={{ color:SEAT_COLORS[s.seatType]?.text || '#888', fontSize:'11px' }}>
                        {s.seatType}
                      </div>
                    </div>
                    <div style={{ color:'#00d4ff', fontWeight:'800' }}>₹{s.price}</div>
                  </div>
                ))}
              </div>

              <div style={{ borderTop:'1px solid #ffffff11', paddingTop:'12px', marginBottom:'16px' }}>
                <div style={{ display:'flex', justifyContent:'space-between', color:'#667799', fontSize:'13px', marginBottom:'4px' }}>
                  <span>Subtotal ({selected.length} seats)</span>
                  <span>₹{totalPrice}</span>
                </div>
                <div style={{ display:'flex', justifyContent:'space-between', color:'#fff', fontSize:'16px', fontWeight:'900' }}>
                  <span>Total</span>
                  <span style={{ color:'#00d4ff' }}>₹{totalPrice}</span>
                </div>
              </div>

              <button
                onClick={handleProceed}
                disabled={booking || showStarted}
                style={{
                  width:'100%', padding:'14px', borderRadius:'12px', border:'none',
                  background: (booking || showStarted) ? '#1a3a3a' : 'linear-gradient(135deg,#00d4ff,#0066ff)',
                  color:'#fff', fontWeight:'900', fontSize:'15px', cursor: booking ? 'not-allowed' : 'pointer',
                  boxShadow: booking ? 'none' : '0 4px 20px #00d4ff44',
                  transition:'all 0.2s',
                }}>
                {showStarted ? 'Show Started' : booking ? '⏳ Booking...' : `💳 Pay ₹${totalPrice}`}
              </button>

              <button
                onClick={() => { setSelected([]); setError(''); }}
                style={{
                  width:'100%', padding:'10px', marginTop:'8px', borderRadius:'10px',
                  background:'transparent', border:'1px solid #ffffff22',
                  color:'#667799', fontSize:'13px', cursor:'pointer',
                }}>
                Clear Selection
              </button>
            </>
          )}

          <div style={{ marginTop:'20px', padding:'12px', background:'rgba(255,255,255,0.02)', borderRadius:'10px' }}>
            <div style={{ color:'#667799', fontSize:'12px', lineHeight:'1.6' }}>
              <div>Available: <span style={{ color:'#00ff88' }}>{data?.availableCount || 0}</span></div>
              <div>Booked: <span style={{ color:'#ff4466' }}>{data?.bookedCount || 0}</span></div>
              <div>Total: <span style={{ color:'#fff' }}>150</span></div>
            </div>
          </div>
        </div>
      </div>
      <style>{`@keyframes spin{to{transform:rotate(360deg)}}`}</style>
    </div>
  );
}

function renderSeat(seat, colors, selected, toggleSeat) {
  const isSelected = selected.find(s => s.id === seat.id);
  const bg = seat.booked ? colors.booked
           : isSelected  ? colors.selected
           :               colors.avail;
  return (
    <div key={seat.id}
      title={`${seat.seatNumber} (${seat.seatType}) ₹${seat.price}${seat.booked?' - BOOKED':''}`}
      onClick={() => toggleSeat(seat)}
      style={{
        width:'28px', height:'28px', borderRadius:'5px 5px 3px 3px',
        background: bg,
        border: isSelected ? `2px solid ${colors.selected}` : '1px solid rgba(255,255,255,0.08)',
        cursor: seat.booked ? 'not-allowed' : 'pointer',
        display:'flex', alignItems:'center', justifyContent:'center',
        fontSize:'9px', color:'rgba(255,255,255,0.5)',
        transition:'transform 0.1s',
        transform: isSelected ? 'scale(1.15)' : 'scale(1)',
      }}>
      {isSelected ? '✓' : ''}
    </div>
  );
}

const spinner = {
  width:'36px', height:'36px', margin:'0 auto',
  border:'3px solid #ffffff11', borderTop:'3px solid #00d4ff',
  borderRadius:'50%', animation:'spin 0.8s linear infinite',
};
