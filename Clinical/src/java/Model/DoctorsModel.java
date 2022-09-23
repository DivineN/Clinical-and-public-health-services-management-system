/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Dao.AppointmentDao;
import Dao.DoctorsDao;
import Dao.OperationDao;
import Dao.UserDao;
import Domain.Appointment;
import Domain.Doctors;
import Domain.Hospital;
import Domain.Operation;
import Domain.Patients;
import Domain.User;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import javax.faces.context.ExternalContext;

/**
 *
 * @author Rugwiro
 */
@ManagedBean
@SessionScoped
public class DoctorsModel {

    private User u = new User();
    private String unipassword = new String();
    private Doctors docdetails = new Doctors();
    private Appointment appdetails = new Appointment();
    DoctorsDao docDao = new DoctorsDao();
    private String id = new String();
    private String searchKey = new String();
    private List<Doctors> docList;
    private Doctors doctors = new Doctors();
    private Doctors load = new Doctors();

    private Operation operation = new Operation();
    private Operation operationdetails = new Operation();
    private OperationDao operationDao = new OperationDao();
    private List<Operation> opList = new OperationDao().findAll(Operation.class);

    List<Doctors> docList2 = new DoctorsDao().view("from Doctors a WHERE a.Id='" + loadName() + "'");
    List<Appointment> appList2 = new AppointmentDao().view("from Appointment a WHERE a.doctors.Id='" + loadName() + "'");
    List<Operation> opList2 = new OperationDao().view("from Operation a WHERE a.doctors.Id='" + loadName() + "'");
    List<Appointment> appList3 = new AppointmentDao().view("from Appointment a WHERE a.doctors.Id='" + loadName() + "' GROUP BY a.patients.Id");
    List<Appointment> appList4 = new AppointmentDao().view("from Appointment a WHERE a.doctors.Id='" + loadName() + "' AND a.Status='Approved' GROUP BY a.patients.Id");
    private List<User> userList = new UserDao().findAll(User.class);
    String NewUserName, OldPass, NewPass, ConfNewPass;

    Integer dropPatients;

    @PostConstruct
    public void init() {
        docList = new DoctorsDao().findAll(Doctors.class);
        loadName();
    }

    public Integer loadName() {
        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        load = x.getDoctors();
        Integer IdNum = load.getId();
        return IdNum;
    }

    public void createDoctor() {

        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        doctors.setHospital(x.getHospital());
        new DoctorsDao().save(doctors);
        u.setPassword(u.getPassword());
        u.setAccess("Doctor");
        u.setDoctors(doctors);
        new UserDao().save(u);

        u = new User();
        doctors = new Doctors();

        docList = new DoctorsDao().findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Doctor Registered!", null));
    }

    public void createOperation() {

        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        operation.setDoctors(x.getDoctors());
        Patients patients = new Patients();
        patients.setId(dropPatients);
        operation.setPatients(patients);
        new OperationDao().save(operation);

        operation = new Operation();

        opList2 = new OperationDao().view("from Operation a WHERE a.doctors.Id='" + loadName() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation Registered!", null));
    }

    public void updateDoctor() {

        docDao.update(docdetails);
        docdetails = new Doctors();
        docList = docDao.findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated!", null));
    }

    public String fetchItems(final Doctors doc) {
        this.docdetails = doc;
        return "EditDoctor.xhtml?faces-redirect=true";
    }

    public void deleteDoctor() {
        docDao.delete(this.docdetails);
        docList = docDao.findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted!", null));
    }

    public String deleteOPeration() {
        operationDao.delete(this.operationdetails);
        opList2 = new OperationDao().view("from Operation a WHERE a.doctors.Id='" + loadName() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation Deleted!", null));
         return "viewOperations.xhtml?faces-redirect=true";
    }

    public void fetchAndShow(final Doctors doc) {
        this.docdetails = doc;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void fetchAndShowOperation(final Operation op) {
        this.operationdetails = op;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void clearOperationDetails() {
        this.operationdetails = null;
    }

    public void clearDoctorDetails() {
        this.docdetails = null;
    }

    public List<Doctors> view() {
        List<Doctors> list = new DoctorsDao().findAll(Doctors.class);
        return list;
    }

    public void updateInfo() {

        userList.forEach((us) -> {
            if (us.getAccess().matches("Doctor") && load.getId().equals(us.getDoctors().getId())) {

                if (!OldPass.equals(us.getPassword())) {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old Password is Incorrect!", null));

                } else if (!NewPass.equals(ConfNewPass)) {
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password do not match!", null));

                } else {

                    u.setId(us.getId());
                    u.setAccess(us.getAccess());
                    u.setDoctors(us.getDoctors());
                    u.setPatients(us.getPatients());
                    u.setHospital(us.getHospital());
                    u.setUsername(us.getUsername());
                    u.setPassword(NewPass);
                    new UserDao().update(u);
                    FacesContext ctx = FacesContext.getCurrentInstance();
                    ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password Changed!", null));

                }

            }

        });

    }

    public void fetchAndShow(final Appointment app) {
        this.appdetails = app;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void approve(Appointment app) throws Exception {

        app.setStatus("Approved");
        new AppointmentDao().update(app);

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage("Appointment Approved!"));
        appList4 = new AppointmentDao().view("from Appointment a WHERE a.doctors.Id='" + loadName() + "' AND a.Status='Approved' GROUP BY a.patients.Id");
    }

    public void deny(Appointment app) throws Exception {
        app.setStatus("Denied");
        new AppointmentDao().update(app);

        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Appointment Denied", null));
        appList4 = new AppointmentDao().view("from Appointment a WHERE a.doctors.Id='" + loadName() + "' AND a.Status='Approved' GROUP BY a.patients.Id");
    }

    public String fetchDoctor(final Doctors doc) {
        this.docdetails = doc;

        return "updateInfo.xhtml?faces-redirect=true";

    }

    public void createoneorderPDF() {
        try {

            FacesContext context = FacesContext.getCurrentInstance();
            Document document = new Document();
            Rectangle rect = new Rectangle(20, 20, 800, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setBoxSize("art", rect);
            document.setPageSize(rect);
            if (!document.isOpen()) {
                document.open();
            }
            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Admin");
            path = path.substring(0, path.indexOf("\\build"));
            path = path + "\\web\\Admin\\img\\logo.png";
            Image image = Image.getInstance(path);
            image.scaleAbsolute(80, 20);
            image.setAlignment(Element.ALIGN_LEFT);
            Paragraph title = new Paragraph();
            //BEGIN page
            title.add(image);
            title.add("\n ORGANS DONOR");
//            title.add("\n Conseil National de Lutte contre le Sida");
            title.add("\n P.O. Box 1189 KIGALI - RWANDA");
            title.add("\n Email: info@organsdor.gov.rw");
            document.add(title);

            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);

            document.add(new Paragraph("\n"));
            Paragraph para = new Paragraph(" Doctor Report List  ", font0);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(new Paragraph("\n"));
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
            cell1.setBorder(Rectangle.BOX);
            table.addCell(cell1);

            PdfPCell cell2 = new PdfPCell(new Phrase("Names", font2));
            cell2.setBorder(Rectangle.BOX);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Phrase("Gender", font2));
            cell3.setBorder(Rectangle.BOX);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Phrase("Phone ", font2));
            cell4.setBorder(Rectangle.BOX);
            table.addCell(cell4);

            PdfPCell cell5 = new PdfPCell(new Phrase("Certification ", font2));
            cell5.setBorder(Rectangle.BOX);
            table.addCell(cell5);
//
//            PdfPCell cell6 = new PdfPCell(new Phrase("Position ", font2));
//            cell6.setBorder(Rectangle.BOX);
//            table.addCell(cell6);

            PdfPCell pdfc1;
            PdfPCell pdfc2;
            PdfPCell pdfc3;
            PdfPCell pdfc4;
            PdfPCell pdfc5;
//            PdfPCell pdfc6;

            int i = 1;

            for (Doctors pat : docList) {
                pdfc1 = new PdfPCell(new Phrase(pat.getId().toString(), font6));
                pdfc1.setBorder(Rectangle.BOX);
                table.addCell(pdfc1);

                pdfc2 = new PdfPCell(new Phrase(pat.getFName() + " " + pat.getLName(), font6));
                pdfc2.setBorder(Rectangle.BOX);
                table.addCell(pdfc2);

                pdfc3 = new PdfPCell(new Phrase(pat.getGender(), font6));
                pdfc3.setBorder(Rectangle.BOX);
                table.addCell(pdfc3);

                pdfc4 = new PdfPCell(new Phrase(pat.getPhone(), font6));
                pdfc4.setBorder(Rectangle.BOX);
                table.addCell(pdfc4);

//                pdfc5 = new PdfPCell(new Phrase(pat.getCertification(), font6));
//                pdfc5.setBorder(Rectangle.BOX);
//                table.addCell(pdfc5);
//                pdfc6 = new PdfPCell(new Phrase(pat.getPosition(), font6));
//                pdfc6.setBorder(Rectangle.BOX);
//                table.addCell(pdfc6);
            }
            document.add(table);
            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
            p.setAlignment(Element.ALIGN_RIGHT);
            document.add(p);
            Paragraph ps = new Paragraph("\n ORGANS DONOR ", font1);
            ps.setAlignment(Element.ALIGN_RIGHT);
            document.add(ps);
            document.close();
            String fileName = "Doctors Report";

            writePDFToResponse(context.getExternalContext(), baos, fileName);

            context.responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    public void createoneorderPDFApp() {
//        try {
//
//            FacesContext context = FacesContext.getCurrentInstance();
//            Document document = new Document();
//            Rectangle rect = new Rectangle(20, 20, 800, 800);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            PdfWriter writer = PdfWriter.getInstance(document, baos);
//            writer.setBoxSize("art", rect);
//            document.setPageSize(rect);
//            if (!document.isOpen()) {
//                document.open();
//            }
//            String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("\\Admin");
//            path = path.substring(0, path.indexOf("\\build"));
//            path = path + "\\web\\Admin\\img\\logo.png";
//            Image image = Image.getInstance(path);
//            image.scaleAbsolute(80, 20);
//            image.setAlignment(Element.ALIGN_LEFT);
//            Paragraph title = new Paragraph();
//            //BEGIN page
//            title.add(image);
//            title.add("\n ORGANS DONOR");
////            title.add("\n Conseil National de Lutte contre le Sida");
//            title.add("\n P.O. Box 1189 KIGALI - RWANDA");
//            title.add("\n Email: info@organsdor.gov.rw");
//            document.add(title);
//
//            Font font0 = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.UNDERLINE);
//            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.BOLD);
//            Font font2 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
//            Font font5 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
//            Font font6 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
//            Font font7 = new Font(Font.FontFamily.TIMES_ROMAN, 11, Font.ITALIC);
//            Font font8 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.UNDERLINE);
//
//            document.add(new Paragraph("\n"));
//            Paragraph para = new Paragraph(" Appointment Report List  ", font0);
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//            document.add(new Paragraph("\n"));
//            PdfPTable table = new PdfPTable(5);
//            table.setWidthPercentage(100);
//
//            PdfPCell cell1 = new PdfPCell(new Phrase("#", font2));
//            cell1.setBorder(Rectangle.BOX);
//            table.addCell(cell1);
//
//            PdfPCell cell2 = new PdfPCell(new Phrase("Patient", font2));
//            cell2.setBorder(Rectangle.BOX);
//            table.addCell(cell2);
//
//            PdfPCell cell3 = new PdfPCell(new Phrase("Doctor", font2));
//            cell3.setBorder(Rectangle.BOX);
//            table.addCell(cell3);
//
//            PdfPCell cell4 = new PdfPCell(new Phrase("Date ", font2));
//            cell4.setBorder(Rectangle.BOX);
//            table.addCell(cell4);
//
//            PdfPCell cell5 = new PdfPCell(new Phrase("Status ", font2));
//            cell5.setBorder(Rectangle.BOX);
//            table.addCell(cell5);
//
//            PdfPCell pdfc1;
//            PdfPCell pdfc2;
//            PdfPCell pdfc3;
//            PdfPCell pdfc4;
//            PdfPCell pdfc5;
//
//            int i = 1;
//
//            for (Appointments appt : appList2= new AppointmentsDao().view("from Appointments a WHERE a.doctors.Id='"+loadName() +"'")) {
//                pdfc1 = new PdfPCell(new Phrase(appt.getId().toString(), font6));
//                pdfc1.setBorder(Rectangle.BOX);
//                table.addCell(pdfc1);
//
//                pdfc2 = new PdfPCell(new Phrase(appt.getPatients().getFName() + " " + appt.getPatients().getLName(), font6));
//                pdfc2.setBorder(Rectangle.BOX);
//                table.addCell(pdfc2);
//
//                pdfc3 = new PdfPCell(new Phrase(appt.getDoctors().getFName() + " " + appt.getDoctors().getLName(), font6));
//                pdfc3.setBorder(Rectangle.BOX);
//                table.addCell(pdfc3);
//
//                pdfc4 = new PdfPCell(new Phrase(appt.getDate().toString(), font6));
//                pdfc4.setBorder(Rectangle.BOX);
//                table.addCell(pdfc4);
//
//                pdfc5 = new PdfPCell(new Phrase(appt.getStatus(), font6));
//                pdfc5.setBorder(Rectangle.BOX);
//                table.addCell(pdfc5);
//
//            }
//            document.add(table);
//            Paragraph p = new Paragraph("\n\nPrinted On: " + new Date(), font1);
//            p.setAlignment(Element.ALIGN_RIGHT);
//            document.add(p);
//            Paragraph ps = new Paragraph("\n ORGANS DONOR ", font1);
//            ps.setAlignment(Element.ALIGN_RIGHT);
//            document.add(ps);
//            document.close();
//            String fileName = "Appointments Report";
//
//            writePDFToResponse(context.getExternalContext(), baos, fileName);
//
//            context.responseComplete();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
    private void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) {
        try {
            externalContext.responseReset();
            externalContext.setResponseContentType("application/pdf");
            externalContext.setResponseHeader("Expires", "0");
            externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            externalContext.setResponseHeader("Pragma", "public");
            externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
            externalContext.setResponseContentLength(baos.size());
            OutputStream out = externalContext.getResponseOutputStream();
            baos.writeTo(out);
            externalContext.responseFlushBuffer();
        } catch (IOException e) {

        }
    }

    public User getU() {
        return u;
    }

    public void setU(User u) {
        this.u = u;
    }

    public String getUnipassword() {
        return unipassword;
    }

    public void setUnipassword(String unipassword) {
        this.unipassword = unipassword;
    }

    public Doctors getDocdetails() {
        return docdetails;
    }

    public void setDocdetails(Doctors docdetails) {
        this.docdetails = docdetails;
    }

    public DoctorsDao getDocDao() {
        return docDao;
    }

    public void setDocDao(DoctorsDao docDao) {
        this.docDao = docDao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public List<Doctors> getDocList() {
        return docList;
    }

    public void setDocList(List<Doctors> docList) {
        this.docList = docList;
    }

    public Doctors getDoctors() {
        return doctors;
    }

    public void setDoctors(Doctors doctors) {
        this.doctors = doctors;
    }

    public Doctors getLoad() {
        return load;
    }

    public void setLoad(Doctors load) {
        this.load = load;
    }

    public List<Appointment> getAppList2() {
        return appList2;
    }

    public void setAppList2(List<Appointment> appList2) {
        this.appList2 = appList2;
    }

    public List<Appointment> getAppList3() {
        return appList3;
    }

    public void setAppList3(List<Appointment> appList3) {
        this.appList3 = appList3;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public String getNewUserName() {
        return NewUserName;
    }

    public void setNewUserName(String NewUserName) {
        this.NewUserName = NewUserName;
    }

    public String getOldPass() {
        return OldPass;
    }

    public void setOldPass(String OldPass) {
        this.OldPass = OldPass;
    }

    public String getNewPass() {
        return NewPass;
    }

    public void setNewPass(String NewPass) {
        this.NewPass = NewPass;
    }

    public String getConfNewPass() {
        return ConfNewPass;
    }

    public void setConfNewPass(String ConfNewPass) {
        this.ConfNewPass = ConfNewPass;
    }

    public List<Doctors> getDocList2() {
        return docList2;
    }

    public void setDocList2(List<Doctors> docList2) {
        this.docList2 = docList2;
    }

    public Appointment getAppdetails() {
        return appdetails;
    }

    public void setAppdetails(Appointment appdetails) {
        this.appdetails = appdetails;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public OperationDao getOperationDao() {
        return operationDao;
    }

    public void setOperationDao(OperationDao operationDao) {
        this.operationDao = operationDao;
    }

    public List<Operation> getOpList() {
        return opList;
    }

    public void setOpList(List<Operation> opList) {
        this.opList = opList;
    }

    public List<Operation> getOpList2() {
        return opList2;
    }

    public void setOpList2(List<Operation> opList2) {
        this.opList2 = opList2;
    }

    public Integer getDropPatients() {
        return dropPatients;
    }

    public void setDropPatients(Integer dropPatients) {
        this.dropPatients = dropPatients;
    }

    public Operation getOperationdetails() {
        return operationdetails;
    }

    public void setOperationdetails(Operation operationdetails) {
        this.operationdetails = operationdetails;
    }

    public List<Appointment> getAppList4() {
        return appList4;
    }

    public void setAppList4(List<Appointment> appList4) {
        this.appList4 = appList4;
    }

}
