/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Dao.AppointmentDao;
import Dao.DoctorsDao;
import Dao.HospitalDao;
import Dao.PatientDao;
import Dao.UserDao;
import Domain.Appointment;
import Domain.Doctors;
import Domain.Hospital;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.context.ExternalContext;

/**
 *
 * @author Rugwiro
 */
@ManagedBean
@SessionScoped
public class PatientsModel {

    private User u = new User();
    private String unipassword = new String();
    private Doctors docdetails = new Doctors();
    private Hospital hosdetails = new Hospital();
    DoctorsDao docDao = new DoctorsDao();
    AppointmentDao appDao = new AppointmentDao();
    PatientDao patDao = new PatientDao();
    private String id = new String();
    private String searchKey = new String();
    private List<Doctors> docList;
    private Doctors doctors = new Doctors();
    private Patients loadPat = new Patients();
    private List<Hospital> hosList = new HospitalDao().findAll(Hospital.class);
    List<Doctors> docLists = new DoctorsDao().view("from Doctors c WHERE c.hospital.Id='" + hosdetails.getId() + "'");
    List<Appointment> appList2 = new AppointmentDao().view("from Appointment a WHERE a.patients.Id='" + loadName() + "'");
    List<Patients> patList2 = new PatientDao().view("from Patients a WHERE a.Id='" + loadName() + "'");

    private Appointment appointment = new Appointment();
    private AppointmentDao appointmentsDao = new AppointmentDao();
    private List<Appointment> appList = new AppointmentDao().findAll(Appointment.class);
    List<Appointment> app = new ArrayList<>();
    private Appointment appdetails = new Appointment();
    private Patients patdetails = new Patients();
    private List<User> userList = new UserDao().findAll(User.class);
    String NewUserName, OldPass, NewPass, ConfNewPass;
    Integer k = 0;

    Appointment appts = new Appointment();

    @PostConstruct
    public void init() {
        docList = new DoctorsDao().findAll(Doctors.class);
        appList = new AppointmentDao().findAll(Appointment.class);
        loadName();
    }

    public Integer loadName() {
        User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
        loadPat = x.getPatients();
        Integer IdNum = loadPat.getId();
        return IdNum;
    }

    public void createAppointment() {

        if (appointment.getApp_date().before(new Date())) {

            FacesContext ctx = FacesContext.getCurrentInstance();
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "The date entered is in the past!", null));
        } else {

            Date date = appointment.getApp_date();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String today = formatter.format(date);

            appList.forEach((apt) -> {
                Date date2 = apt.getApp_date();
                String today2 = formatter.format(date2);

                if (today.matches(today2)) {

                    k = 1;

                    appts = apt;
                    System.out.println(today);
                    System.out.println(today2);
                    System.out.println(appts.getId());

                }

            });

            if (k == 1 && !appts.getStatus().matches("Denied") && appts.getDoctors().getId().toString().matches(docdetails.getId().toString())) {

                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "This date is already taken!", null));
                k = 0;
            } else {
                Doctors doctors = new Doctors();
                doctors.setId(docdetails.getId());
                appointment.setDoctors(doctors);
                User x = (User) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("session");
                appointment.setPatients(x.getPatients());

                appointmentsDao.save(appointment);
                appList2 = new AppointmentDao().view("from Appointment a WHERE a.patients.Id='" + loadName() + "'");
                appList = new AppointmentDao().findAll(Appointment.class);
                appointment = new Appointment();
                FacesContext ctx = FacesContext.getCurrentInstance();
                ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Appointment Created!", null));
               k = 0;
            }
        }

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

    public void updateDoctor() {

        docDao.update(docdetails);
        docdetails = new Doctors();
        docList = docDao.findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated!", null));
    }

    public void updatePatient() {

        patDao.update(patdetails);
        patdetails = new Patients();
        patList2 = new PatientDao().view("from Patients a WHERE a.Id='" + loadName() + "'");
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info Updated!", null));
    }

    public String fetchItems(final Doctors doc) {
        this.docdetails = doc;
        return "viewDoctor.xhtml?faces-redirect=true";
    }

    public void deleteDoctor() {
        docDao.delete(this.docdetails);
        docList = docDao.findAll(Doctors.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted!", null));
    }

    public String deleteAppointment() {
        appDao.delete(this.appdetails);
        appList2 = new AppointmentDao().view("from Appointment a WHERE a.patients.Id='" + loadName() + "'");
        appList = new AppointmentDao().findAll(Appointment.class);
        FacesContext ctx = FacesContext.getCurrentInstance();
        ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Deleted!", null));
        return "viewAppointments.xhtml?faces-redirect=true";
    }

    public void fetchAndShow(final Appointment app) {
        this.appdetails = app;
        RequestContext.getCurrentInstance().execute("PF('delete').show()");
    }

    public void clearAppointmentDetails() {
        this.appdetails = null;
    }

    public void clearDoctorDetails() {
        this.docdetails = null;
    }

    public List<Doctors> view() {
        List<Doctors> list = new DoctorsDao().findAll(Doctors.class);
        return list;
    }

    public String fetchHospital(final Hospital hos) {
        this.hosdetails = hos;
        docLists = new DoctorsDao().view("from Doctors c WHERE c.hospital.Id='" + hosdetails.getId() + "'");
        return "viewDoctor.xhtml?faces-redirect=true";

    }

    public String fetchDoctor(final Doctors doc) {
        this.docdetails = doc;

        return "requestAppointment.xhtml?faces-redirect=true";

    }

    public String fetchPatient(final Patients pat) {
        this.patdetails = pat;

        return "updateInfo.xhtml?faces-redirect=true";

    }

    public void updateInfo() {

        userList.forEach((us) -> {
            if (us.getAccess().matches("Patient") && loadPat.getId().equals(us.getPatients().getId())) {

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

    public Patients getLoadPat() {
        return loadPat;
    }

    public void setLoadPat(Patients loadPat) {
        this.loadPat = loadPat;
    }

    public List<Hospital> getHosList() {
        return hosList;
    }

    public void setHosList(List<Hospital> hosList) {
        this.hosList = hosList;
    }

    public Hospital getHosdetails() {
        return hosdetails;
    }

    public void setHosdetails(Hospital hosdetails) {
        this.hosdetails = hosdetails;
    }

    public List<Doctors> getDocLists() {
        return docLists;
    }

    public void setDocLists(List<Doctors> docLists) {
        this.docLists = docLists;
    }

    public List<Appointment> getAppList2() {
        return appList2;
    }

    public void setAppList2(List<Appointment> appList2) {
        this.appList2 = appList2;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public AppointmentDao getAppointmentsDao() {
        return appointmentsDao;
    }

    public void setAppointmentsDao(AppointmentDao appointmentsDao) {
        this.appointmentsDao = appointmentsDao;
    }

    public List<Appointment> getAppList() {
        return appList;
    }

    public void setAppList(List<Appointment> appList) {
        this.appList = appList;
    }

    public List<Appointment> getApp() {
        return app;
    }

    public void setApp(List<Appointment> app) {
        this.app = app;
    }

    public Appointment getAppdetails() {
        return appdetails;
    }

    public void setAppdetails(Appointment appdetails) {
        this.appdetails = appdetails;
    }

    public List<Patients> getPatList2() {
        return patList2;
    }

    public void setPatList2(List<Patients> patList2) {
        this.patList2 = patList2;
    }

    public Patients getPatdetails() {
        return patdetails;
    }

    public void setPatdetails(Patients patdetails) {
        this.patdetails = patdetails;
    }

    public PatientDao getPatDao() {
        return patDao;
    }

    public void setPatDao(PatientDao patDao) {
        this.patDao = patDao;
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

}
