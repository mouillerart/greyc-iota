<%

            try {
                if (session.getAttribute("sessionID") != null) {
                    String sessionID = (String) session.getAttribute("sessionID");
                    if (!fr.unicaen.iota.discovery.server.util.Session.isValidSession(sessionID)) {
                        session.removeAttribute("sessionID");
                    }
                }
            } catch (IllegalStateException ise) {
            }

%>
