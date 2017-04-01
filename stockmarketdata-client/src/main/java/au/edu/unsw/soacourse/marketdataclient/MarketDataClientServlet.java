package au.edu.unsw.soacourse.marketdataclient;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MarketDataClientServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/jsp/index.jsp");
		rd.forward(request, response);

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String action = request.getParameter("action");
		System.out.println(action);
		
		response.setContentType("text/html;charset=UTF-8");
		RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/jsp/index.jsp");
		rd.forward(request, response);
	}

}
