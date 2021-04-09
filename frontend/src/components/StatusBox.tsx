

export default function StatusBox(props:{color:string, text:string}) {

    let { color, text } = props

    return <span style={{
        backgroundColor: color,
        padding: '0 10px',
        lineHeight: '1.5',
        fontSize: '.875rem',
        color: '#fff',
        verticalAlign: 'middle',
        whiteSpace: 'nowrap',
        textAlign: 'center',
        borderRadius: '0.6rem',
        textTransform: 'uppercase',
    }}>{text}</span>
}



