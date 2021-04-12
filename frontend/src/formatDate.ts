
import { formatRelative, parseISO } from 'date-fns'

export default function formatDate(dateStr:string) {
    return formatRelative(parseISO(dateStr), new Date())
}

