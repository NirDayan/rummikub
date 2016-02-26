package servlets.gameplay;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.utils.ServletUtils;
import servlets.utils.SessionUtils;

@WebServlet(name = "moveTileServlet", urlPatterns = {"/moveTile"})
public class moveTileServlet extends HttpServlet {

    private static final String SOURCE_SEQUENCE_INDEX = "sourceSequenceIndex";
    private static final String SOURCE_SEQUENCE_POSITION = "sourceSequencePosition";
    private static final String TARGET_SEQUENCE_INDEX = "targetSequenceIndex";
    private static final String TARGET_SEQUENCE_POSITION = "targetSequencePosition";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int sourceSequenceIndex = Integer.parseInt(request.getParameter(SOURCE_SEQUENCE_INDEX));
        int sourceSequencePosition = Integer.parseInt(request.getParameter(SOURCE_SEQUENCE_POSITION));
        int targetSequenceIndex = Integer.parseInt(request.getParameter(TARGET_SEQUENCE_INDEX));
        int targetSequencePosition = Integer.parseInt(request.getParameter(TARGET_SEQUENCE_POSITION));

        try {
            ServletUtils.getWebService(getServletContext())
                    .moveTile(SessionUtils.getPlayerId(request), sourceSequenceIndex, sourceSequencePosition, targetSequenceIndex, targetSequencePosition);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception ex) {
            response.getWriter().write(ex.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
